package younesbouhouche.musicplayer.main.presentation.viewmodel

import android.content.Context
import android.media.AudioManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.ColsCount
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.UiPlaylist
import younesbouhouche.musicplayer.core.domain.util.stateInVM
import younesbouhouche.musicplayer.main.presentation.util.search
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.data.models.Queue
import younesbouhouche.musicplayer.main.data.util.getVolume
import younesbouhouche.musicplayer.main.domain.events.FilesEvent
import younesbouhouche.musicplayer.main.domain.events.FilesEvent.LoadFiles
import younesbouhouche.musicplayer.main.domain.events.MetadataEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.events.SearchEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.Routes
import younesbouhouche.musicplayer.main.domain.repo.FilesRepo
import younesbouhouche.musicplayer.main.presentation.constants.Permissions
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.states.PlaylistViewState
import younesbouhouche.musicplayer.main.presentation.states.SearchState
import younesbouhouche.musicplayer.main.presentation.states.StartupEvent
import younesbouhouche.musicplayer.main.presentation.states.UiState
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType
import younesbouhouche.musicplayer.main.presentation.util.isPermissionGranted

@OptIn(ExperimentalCoroutinesApi::class)
class MainVM(
    context: Context,
    val filesRepo: FilesRepo,
    val dao: AppDao,
    audioManager: AudioManager,
) : ViewModel() {
    private fun <T> Flow<T>.stateInVM(initialValue: T) = stateInVM(initialValue, viewModelScope)

    private val _files = filesRepo.getFiles().stateInVM(emptyList())
    private val _albums = filesRepo.getAlbums().stateInVM(emptyList())
    private val _artists = filesRepo.getArtists().stateInVM(emptyList())

    private var startupEvent: StartupEvent = StartupEvent.None
    private val isGranted = context.isPermissionGranted(Permissions.audioPermission)

    private val _granted = MutableStateFlow(isGranted)
    val granted = _granted.stateInVM(isGranted)

    private var initialized = false

    private fun List<Long>.toMusicCards() =
        mapNotNull { id -> _files.value.firstOrNull { it.id == id } }

    private val _playlists = filesRepo.getPlaylists()
    val playlists = _playlists.stateInVM(emptyList())

    private val _queue = dao.getQueue().map { it ?: Queue() }

    private val _queueList = _queue.map { it.items }.stateInVM(emptyList())

    val queueIndex = _queue.map { it.index }.stateInVM(-1)

    val queue = _queue.stateInVM(Queue())

    val queueFiles =
        combine(_queueList, _files) { ids, files ->
            ids.mapNotNull { id -> files.firstOrNull { it.id == id } }
        }.stateInVM(emptyList())

    private val _listScreenFiles = MutableStateFlow(emptyList<MusicCard>())

    private val _searchState = MutableStateFlow(SearchState())
    val searchState =
        combine(_searchState, _files) { state, files ->
            val results =
                if (state.query.isNotBlank()) {
                    files.filter { it.search(state.query) }
                } else {
                    emptyList()
                }
            state.copy(result = results)
        }.stateInVM(SearchState())

    private val _sortState = MutableStateFlow(SortState(SortType.Title))
    val sortState = _sortState.asStateFlow()

    private val _listScreenSortState = MutableStateFlow(SortState(SortType.Title))
    val listScreenSortState = _listScreenSortState.asStateFlow()

    private val _albumsSortState = MutableStateFlow(SortState(ListsSortType.Name, ColsCount.One))
    val albumsSortState = _albumsSortState.asStateFlow()

    private val _artistsSortState = MutableStateFlow(SortState(ListsSortType.Name, ColsCount.One))
    val artistsSortState = _artistsSortState.asStateFlow()

    private val _playlistsSortState = MutableStateFlow(SortState(ListsSortType.Name, ColsCount.One))
    val playlistsSortState = _playlistsSortState.asStateFlow()

    private val _playlistSortState = MutableStateFlow(SortState(PlaylistSortType.Custom))
    val playlistSortState = _playlistSortState.asStateFlow()

    private val _playlistIndex = MutableStateFlow(0)

    val lastAdded =
        _files
            .map { item -> item.sortedByDescending { it.date } }
            .stateInVM(emptyList())

    private val _timestamps = dao.getTimestamps().stateInVM(emptyList())

    val history = combine(_files, _timestamps) { files, timestamps ->
        timestamps
            .sortedByDescending { it.times.maxOrNull() }
            .mapNotNull { item -> files.firstOrNull { item.path == it.path } }
    }.stateInVM(emptyList())

    val mostPlayed =
        combine(_files, _timestamps) { files, timestamps ->
            timestamps
                .sortedByDescending { it.times.size }
                .mapNotNull { item -> files.firstOrNull { item.path == it.path } }
        }.stateInVM(emptyList())

    val filesSorted =
        combine(_files, _sortState) { files, sortState ->
            if (sortState.ascending) {
                when (sortState.sortType) {
                    SortType.Title -> files.sortedBy { it.title }
                    SortType.Filename -> files.sortedBy { it.path }
                    SortType.Duration -> files.sortedBy { it.duration }
                    SortType.Date -> files.sortedBy { it.date }
                    SortType.Size -> TODO()
                }
            } else {
                when (sortState.sortType) {
                    SortType.Title -> files.sortedByDescending { it.title }
                    SortType.Filename -> files.sortedByDescending { it.path }
                    SortType.Duration -> files.sortedByDescending { it.duration }
                    SortType.Date -> files.sortedByDescending { it.date }
                    SortType.Size -> TODO()
                }
            }
        }.stateInVM(emptyList())

    private val _mostPlayedArtists =
        combine(_artists, _files, _timestamps) { artists, files, timestamps ->
            artists
                .asSequence()
                .filter { it.name != "<unknown>" }
                .map { artist ->
                    artist to
                            artist.items
                                .mapNotNull { item -> files.firstOrNull { it.id == item } }
                                .sumOf { item ->
                                    timestamps.firstOrNull { item.path == it.path }
                                        ?.times
                                        ?.size ?: 0
                                }
                }
                .filter { it.second > 0 }
                .sortedByDescending { it.second }
                .map { it.first }
                .toList()
        }
    val mostPlayedArtists =
        _mostPlayedArtists
            .stateInVM(emptyList())

    val listScreenFiles =
        combine(_listScreenFiles, _listScreenSortState) { listScreenFiles, sortState ->
            if (sortState.ascending) {
                when (sortState.sortType) {
                    SortType.Title -> listScreenFiles.sortedBy { it.title }
                    SortType.Filename -> listScreenFiles.sortedBy { it.path }
                    SortType.Duration -> listScreenFiles.sortedBy { it.duration }
                    SortType.Date -> listScreenFiles.sortedBy { it.date }
                    SortType.Size -> TODO()
                }
            } else {
                when (sortState.sortType) {
                    SortType.Title -> listScreenFiles.sortedByDescending { it.title }
                    SortType.Filename -> listScreenFiles.sortedByDescending { it.path }
                    SortType.Duration -> listScreenFiles.sortedByDescending { it.duration }
                    SortType.Date -> listScreenFiles.sortedByDescending { it.date }
                    SortType.Size -> TODO()
                }
            }
        }.stateInVM(emptyList())

    private val _favorites = dao.getFavorites()
    private val _favoritesFiles =
        combine(_files, _favorites) { files, favorites ->
            files.filter { favorites.contains(it.path) }
        }
    val favoritesFiles =
        combine(_favoritesFiles, _listScreenSortState) { listScreenFiles, sortState ->
            if (sortState.ascending) {
                when (sortState.sortType) {
                    SortType.Title -> listScreenFiles.sortedBy { it.title }
                    SortType.Filename -> listScreenFiles.sortedBy { it.path }
                    SortType.Duration -> listScreenFiles.sortedBy { it.duration }
                    SortType.Date -> listScreenFiles.sortedBy { it.date }
                    SortType.Size -> TODO()
                }
            } else {
                when (sortState.sortType) {
                    SortType.Title -> listScreenFiles.sortedByDescending { it.title }
                    SortType.Filename -> listScreenFiles.sortedByDescending { it.path }
                    SortType.Duration -> listScreenFiles.sortedByDescending { it.duration }
                    SortType.Date -> listScreenFiles.sortedByDescending { it.date }
                    SortType.Size -> TODO()
                }
            }
        }.stateInVM(emptyList())

    val albumsSorted =
        combine(_albums, _albumsSortState) { albums, sortState ->
            if (sortState.ascending) {
                when (sortState.sortType) {
                    ListsSortType.Name -> albums.toList().sortedBy { it.title }
                    ListsSortType.Count -> albums.toList().sortedBy { it.items.size }
                }
            } else {
                when (sortState.sortType) {
                    ListsSortType.Name -> albums.toList().sortedByDescending { it.title }
                    ListsSortType.Count -> albums.toList().sortedByDescending { it.items.size }
                }
            }
        }.stateInVM(emptyList())

    val artistsSorted =
        combine(_artists, _artistsSortState) { artists, sortState ->
            if (sortState.ascending) {
                when (sortState.sortType) {
                    ListsSortType.Name -> artists.toList().sortedBy { it.name }
                    ListsSortType.Count -> artists.toList().sortedBy { it.items.size }
                }
            } else {
                when (sortState.sortType) {
                    ListsSortType.Name -> artists.toList().sortedByDescending { it.name }
                    ListsSortType.Count -> artists.toList().sortedByDescending { it.items.size }
                }
            }
        }.stateInVM(emptyList())

    private val _playlistsSorted =
        combine(_playlists, _playlistsSortState) { playlists, sortState ->
            if (sortState.ascending) {
                when (sortState.sortType) {
                    ListsSortType.Name -> playlists.sortedBy { it.name }
                    ListsSortType.Count -> playlists.sortedBy { it.items.size }
                }
            } else {
                when (sortState.sortType) {
                    ListsSortType.Name -> playlists.sortedByDescending { it.name }
                    ListsSortType.Count -> playlists.sortedByDescending { it.items.size }
                }
            }
        }.stateInVM(emptyList())

    private val _playlist =
        combine(_playlistsSorted, _playlistIndex) { playlists, index ->
            playlists.getOrNull(index) ?: Playlist()
        }

    val bottomSheetPlaylist =
        combine(_playlistsSorted, _playlistIndex) { playlists, index ->
            playlists.getOrNull(index) ?: Playlist()
        }.stateInVM(Playlist())

    val uiPlaylist =
        combine(
            _playlist,
            _files,
            _playlistSortState,
        ) { playlist, files, sortState ->
            val list = playlist.items.mapNotNull { item -> files.firstOrNull { it.path == item } }
            val files =
                if (sortState.ascending) {
                    when (sortState.sortType) {
                        PlaylistSortType.Custom -> list
                        PlaylistSortType.Title -> list.sortedBy { it.title }
                        PlaylistSortType.Duration -> list.sortedBy { it.duration }
                        PlaylistSortType.Filename -> list.sortedBy { it.path }
                        PlaylistSortType.Size -> TODO()
                        PlaylistSortType.Date -> TODO()
                    }
                } else {
                    when (sortState.sortType) {
                        PlaylistSortType.Custom -> list.reversed()
                        PlaylistSortType.Title -> list.sortedByDescending { it.title }
                        PlaylistSortType.Duration -> list.sortedByDescending { it.duration }
                        PlaylistSortType.Filename -> list.sortedByDescending { it.path }
                        PlaylistSortType.Size -> TODO()
                        PlaylistSortType.Date -> TODO()
                    }
                }
            UiPlaylist(
                id = playlist.id,
                name = playlist.name,
                image = playlist.image,
                items = files,
            )
        }.stateInVM(UiPlaylist())

    val bottomSheetPlaylistFiles =
        combine(_playlistsSorted, _files, _playlistIndex) { playlists, files, index ->
            playlists.getOrNull(index)?.items?.mapNotNull { item -> files.firstOrNull { it.path == item } }
                ?: emptyList()
        }.stateInVM(emptyList())

    val playlistsSorted = _playlistsSorted.stateInVM(emptyList())

    fun onPlayerEvent(event: PlayerEvent) = viewModelScope.launch {
        filesRepo.onPlayerEvent(event)
    }

    fun getAlbum(file: MusicCard): Album? = _albums.value.firstOrNull { it.title == file.album }

    fun getArtist(file: MusicCard): Artist? = _artists.value.firstOrNull { it.name == file.artist }

    fun setGranted(intent: StartupEvent = StartupEvent.None) {
        viewModelScope.launch {
            _granted.value = true
            if (!initialized) filesRepo.onFilesEvent(LoadFiles)
            startupEvent = intent
        }
    }

    fun onSearchEvent(event: SearchEvent) {
        when (event) {
            SearchEvent.ClearQuery -> {
                _searchState.update {
                    it.copy(query = "")
                }
            }

            is SearchEvent.UpdateQuery -> {
                _searchState.update {
                    it.copy(query = event.query)
                }
            }

            SearchEvent.Collapse -> {
                _searchState.update {
                    it.copy(expanded = false, query = "")
                }
            }

            SearchEvent.Expand -> {
                _searchState.update {
                    it.copy(expanded = true)
                }
            }

            is SearchEvent.UpdateExpanded -> {
                _searchState.update {
                    it.copy(expanded = event.expanded, query = "")
                }
            }
        }
    }

    fun setCurrentPlaylist(index: Int) {
        _playlistIndex.value = index
    }

    fun setListFiles(list: List<Long>) {
        _listScreenFiles.value = list.mapNotNull { id -> _files.value.firstOrNull { it.id == id } }
    }

    fun onListScreenSortChange(state: SortState<SortType>) {
        _listScreenSortState.value = state
    }

    fun onPlaylistScreenSortChange(state: SortState<PlaylistSortType>) {
        _playlistSortState.value = state
    }

    fun onLibrarySortChange(state: SortState<SortType>) {
        _sortState.value = state
    }

    fun onAlbumsSortChange(state: SortState<ListsSortType>) {
        _albumsSortState.value = state
    }

    fun onArtistsSortChange(state: SortState<ListsSortType>) {
        _artistsSortState.value = state
    }

    fun onPlaylistsSortChange(state: SortState<ListsSortType>) {
        _playlistsSortState.value = state
    }

    protected fun finalize() {
        filesRepo.finalize()
    }

    init {
        filesRepo.init(viewModelScope) {
            when (startupEvent) {
                StartupEvent.PlayFavorites ->
                    viewModelScope.launch {
                        val favorites = dao.suspendGetFavorites()
                        onPlayerEvent(PlayerEvent.Play(_files.value.filter { favorites.contains(it.path) }))
                    }

                StartupEvent.PlayMostPlayed ->
                    viewModelScope.launch {
                        val timestamps = dao.suspendGetTimestamps()
                        val mostPlayed =
                            timestamps.toList().sortedByDescending { it.times.size }.map { it.path }
                        onPlayerEvent(PlayerEvent.Play(_files.value.filter { mostPlayed.contains(it.path) }))
                    }

                is StartupEvent.PlayPlaylist ->
                    viewModelScope.launch {
                        val p =
                            playlists.value
                                .firstOrNull { it.id == (startupEvent as StartupEvent.PlayPlaylist).id }
                                ?.items
                                ?.mapNotNull { item -> _files.value.firstOrNull { item == it.path } }
                        println(p)
                        p?.let {
                            onPlayerEvent(PlayerEvent.Play(it))
                        }
                    }

                else -> {}
            }
        }
    }

    private val _playerState = filesRepo.getState()
    val playerState = _playerState.stateInVM(PlayerState(volume = audioManager.getVolume()))
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.stateInVM(UiState())

    fun onUiEvent(event: UiEvent) {
        when (event) {
            UiEvent.ShowSpeedDialog ->
                _uiState.update {
                    it.copy(speedDialog = true)
                }

            UiEvent.HideSpeedDialog ->
                _uiState.update {
                    it.copy(speedDialog = false)
                }

            UiEvent.HideTimerDialog ->
                _uiState.update {
                    it.copy(timerDialog = false)
                }

            UiEvent.ShowTimerDialog ->
                _uiState.update {
                    it.copy(timerDialog = true)
                }

            is UiEvent.SetViewState -> _uiState.update { it.copy(viewState = event.viewState) }
            UiEvent.CollapsePlaylist ->
                _uiState.update { it.copy(playlistViewState = PlaylistViewState.COLLAPSED) }

            UiEvent.ExpandPlaylist ->
                _uiState.update { it.copy(playlistViewState = PlaylistViewState.EXPANDED) }

            is UiEvent.SetPlaylistViewState ->
                _uiState.update { it.copy(playlistViewState = event.playlistViewState) }

            UiEvent.HideBottomSheet ->
                _uiState.update {
                    it.copy(bottomSheetVisible = false)
                }

            is UiEvent.ShowBottomSheet ->
                _uiState.update {
                    it.copy(bottomSheetItem = event.item, bottomSheetVisible = true)
                }

            UiEvent.HideListBottomSheet ->
                _uiState.update {
                    it.copy(listBottomSheetVisible = false)
                }

            is UiEvent.ShowListBottomSheet ->
                _uiState.update {
                    it.copy(
                        listBottomSheetList = event.list.toMusicCards(),
                        listBottomSheetTitle = event.title,
                        listBottomSheetText = event.text,
                        listBottomSheetImage = event.image,
                        listBottomSheetIcon = event.icon,
                        listBottomSheetVisible = true,
                    )
                }

            UiEvent.HideNewPlaylistDialog ->
                _uiState.update {
                    it.copy(newPlaylistDialog = false)
                }

            is UiEvent.ShowCreatePlaylistDialog ->
                _uiState.update {
                    it.copy(
                        newPlaylistDialog = true,
                        newPlaylistName = "",
                        newPlaylistItems = event.items,
                        newPlaylistImage = null
                    )
                }

            is UiEvent.UpdateNewPlaylistName ->
                _uiState.update {
                    it.copy(newPlaylistName = event.newName)
                }

            is UiEvent.ShowAddToPlaylistDialog ->
                _uiState.update {
                    it.copy(addToPlaylistDialog = true, addToPlaylistItems = event.items)
                }

            UiEvent.HideAddToPlaylistDialog ->
                _uiState.update {
                    it.copy(addToPlaylistDialog = false)
                }

            is UiEvent.UpdateSelectedPlaylist ->
                _uiState.update {
                    it.copy(addToPlaylistIndex = event.index)
                }

            UiEvent.HidePlaylistBottomSheet ->
                _uiState.update {
                    it.copy(playlistBottomSheetVisible = false)
                }

            is UiEvent.ShowPlaylistBottomSheet -> {
                viewModelScope.launch {
                    _playlistIndex.value = event.index
                }
                _uiState.update {
                    it.copy(playlistBottomSheetVisible = true)
                }
            }

            UiEvent.HideMetadataDialog ->
                _uiState.update {
                    it.copy(metadataDialog = false)
                }

            is UiEvent.ShowMetadataDialog ->
                _uiState.update {
                    it.copy(
                        metadataDialog = true,
                        metadata = event.metadata,
                    )
                }

            UiEvent.ToggleLyrics ->
                _uiState.update {
                    it.copy(lyricsVisible = !it.lyricsVisible)
                }

            UiEvent.DisableSyncing ->
                _uiState.update {
                    it.copy(syncing = false)
                }

            UiEvent.EnableSyncing ->
                _uiState.update {
                    it.copy(syncing = true)
                }

            UiEvent.DismissDetails ->
                _uiState.update {
                    it.copy(detailsDialog = false)
                }

            is UiEvent.ShowDetails ->
                _uiState.update {
                    it.copy(detailsDialog = true, detailsFile = event.file)
                }

            UiEvent.HideRenamePlaylistDialog ->
                _uiState.update {
                    it.copy(renamePlaylistDialogVisible = false)
                }

            is UiEvent.ShowRenamePlaylistDialog ->
                _uiState.update {
                    it.copy(
                        renamePlaylistDialogVisible = true,
                        renamePlaylistId = event.id,
                        renamePlaylistName = event.name,
                    )
                }

            is UiEvent.UpdateRenamePlaylistName ->
                _uiState.update {
                    it.copy(renamePlaylistName = event.newName)
                }

            UiEvent.HideQueueBottomSheet ->
                _uiState.update {
                    it.copy(queueSheetVisible = false)
                }

            UiEvent.ShowQueueBottomSheet ->
                _uiState.update {
                    it.copy(queueSheetVisible = true)
                }

            UiEvent.HidePitchDialog ->
                _uiState.update {
                    it.copy(pitchDialog = false)
                }

            UiEvent.ShowPitchDialog ->
                _uiState.update {
                    it.copy(pitchDialog = true)
                }

            is UiEvent.UpdateNewPlaylistImage ->
                _uiState.update {
                    it.copy(newPlaylistImage = event.image)
                }

            is UiEvent.ShowSortSheet -> {
                when(event.route) {
                    Routes.Home -> _sortState.update {
                        it.copy(expanded = true)
                    }
                    Routes.Albums -> _albumsSortState.update {
                        it.copy(expanded = true)
                    }
                    Routes.Artists -> _artistsSortState.update {
                        it.copy(expanded = true)
                    }
                    Routes.Playlists -> _playlistsSortState.update {
                        it.copy(expanded = true)
                    }
                    Routes.Library -> _sortState.update {
                        it.copy(expanded = true)
                    }
                }
            }
        }
    }

    fun onMetadataEvent(event: MetadataEvent) {
        // generate when statement of event parameter
        when (event) {
            is MetadataEvent.Album ->
                _uiState.update {
                    it.copy(
                        metadata =
                        it.metadata.copy(
                            newAlbum = event.album,
                        ),
                    )
                }

            is MetadataEvent.Artist ->
                _uiState.update {
                    it.copy(
                        metadata =
                        it.metadata.copy(
                            newArtist = event.artist,
                        ),
                    )
                }

            is MetadataEvent.Composer ->
                _uiState.update {
                    it.copy(
                        metadata =
                        it.metadata.copy(
                            newComposer = event.composer,
                        ),
                    )
                }

            is MetadataEvent.Genre ->
                _uiState.update {
                    it.copy(
                        metadata =
                        it.metadata.copy(
                            newGenre = event.genre,
                        ),
                    )
                }

            is MetadataEvent.Title ->
                _uiState.update {
                    it.copy(
                        metadata =
                        it.metadata.copy(
                            newTitle = event.title,
                        ),
                    )
                }

            is MetadataEvent.Year ->
                _uiState.update {
                    it.copy(
                        metadata =
                        it.metadata.copy(
                            newYear = event.year,
                        ),
                    )
                }
        }
    }

    fun onFilesEvent(event: FilesEvent) = viewModelScope.launch {
        filesRepo.onFilesEvent(event)
    }

    fun onPlaylistEvent(event: PlaylistEvent) = viewModelScope.launch {
        filesRepo.onPlaylistEvent(event)
    }
}
