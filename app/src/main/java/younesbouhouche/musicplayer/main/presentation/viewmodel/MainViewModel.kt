package younesbouhouche.musicplayer.main.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.ColsCount
import younesbouhouche.musicplayer.core.domain.models.UiPlaylist
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.core.domain.util.stateInVM
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistsUiEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.QueueModel
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.main.domain.use_cases.GetAlbumsUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetArtistsUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetFavoritesUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetHistoryUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetLastAddedUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetMediaByIdUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetMostPlayedArtistsUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetMostPlayedUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetPlaylistUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetQueueUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetSortedMediaUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetSortedPlaylistUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.GetSortedPlaylistsUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.PlaybackControlUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.PlaylistControlUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.RefreshUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.SetFavoriteUseCase
import younesbouhouche.musicplayer.main.domain.use_cases.UiControlUseCase
import younesbouhouche.musicplayer.main.presentation.constants.Permissions
import younesbouhouche.musicplayer.main.presentation.models.AlbumUi
import younesbouhouche.musicplayer.main.presentation.models.ArtistUi
import younesbouhouche.musicplayer.main.presentation.states.StartupEvent
import younesbouhouche.musicplayer.main.presentation.states.UiState
import younesbouhouche.musicplayer.main.presentation.util.Event
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType
import younesbouhouche.musicplayer.main.presentation.util.isPermissionGranted
import younesbouhouche.musicplayer.main.presentation.util.sendEvent
import younesbouhouche.musicplayer.main.util.sortBy

class MainViewModel(
    context: Context,
    val refreshUseCase: RefreshUseCase,
    getSortedMediaUseCase: GetSortedMediaUseCase,
    val getMediaByIdUseCase: GetMediaByIdUseCase,
    getFavoritesUseCase: GetFavoritesUseCase,
    getHistoryUseCase: GetHistoryUseCase,
    getLastAddedUseCase: GetLastAddedUseCase,
    getMostPlayedUseCase: GetMostPlayedUseCase,
    getAlbumsUseCase: GetAlbumsUseCase,
    getArtistsUseCase: GetArtistsUseCase,
    getMostPlayedArtistsUseCase: GetMostPlayedArtistsUseCase,
    val playbackControlUseCase: PlaybackControlUseCase,
    val playlistControlUseCase: PlaylistControlUseCase,
    val uiControlUseCase: UiControlUseCase,
    getQueueUseCase: GetQueueUseCase,
    getSortedPlaylistsUseCase: GetSortedPlaylistsUseCase,
    val getPlaylistUseCase: GetPlaylistUseCase,
    getSortedPlaylistUseCase: GetSortedPlaylistUseCase,
    val setFavoriteUseCase: SetFavoriteUseCase,
    stateManager: PlayerStateManager,
    val mediaRepository: MediaRepository
): ViewModel() {
    private fun <T> Flow<T>.stateInVM(initialValue: T) = stateInVM(initialValue, viewModelScope)

    private var startupEvent: StartupEvent = StartupEvent.None
    private var initialized = false
    private val isGranted = context.isPermissionGranted(Permissions.audioPermission)
    private val _granted = MutableStateFlow(isGranted)
    val granted = _granted.stateInVM(isGranted)

    val playerState = stateManager.playerState

    private val _uiState = MutableStateFlow(UiState())
    private val _loadingState = mediaRepository.getLoading()
    val uiState = combine(_uiState, _loadingState) { state, loading ->
        state.copy(loading = loading)
    }.stateInVM(UiState())

    private val _files = mediaRepository.getAllMedia()

    val bottomSheetItem = combine(_uiState.map { it.bottomSheetItem }, _files) { id, files ->
        files.firstOrNull { it.id == id }
    }.stateInVM(null)
    private val _sortState = MutableStateFlow(SortState(SortType.Title))
    val sortState = _sortState.stateInVM(SortState(SortType.Title))
    private val _albumsSortState = MutableStateFlow(
        SortState(ListsSortType.Name, ColsCount.One)
    )
    val albumsSortState = _albumsSortState.asStateFlow()
    private val _artistsSortState = MutableStateFlow(
        SortState(ListsSortType.Name, ColsCount.One)
    )
    val artistsSortState = _artistsSortState.asStateFlow()
    private val _playlistsSortState = MutableStateFlow(
        SortState(ListsSortType.Name, ColsCount.One)
    )
    val playlistsSortState = _playlistsSortState.asStateFlow()
    private val _playlistSortState = MutableStateFlow(SortState(PlaylistSortType.Custom))
    val playlistSortState = _playlistSortState.asStateFlow()

    private val _listScreenSortState = MutableStateFlow(SortState(SortType.Title))
    val listScreenSortState = _listScreenSortState.asStateFlow()

    val files = getSortedMediaUseCase(_sortState).stateInVM(emptyList())
    val favorites = getFavoritesUseCase(_sortState).stateInVM(emptyList())
    val history = getHistoryUseCase().stateInVM(emptyList())
    val lastAdded = getLastAddedUseCase().stateInVM(emptyList())
    val mostPlayed = getMostPlayedUseCase().stateInVM(emptyList())
    val queue = getQueueUseCase().stateInVM(QueueModel())
    val playlists = getSortedPlaylistsUseCase(_playlistsSortState).stateInVM(emptyList())
    val playlist = getSortedPlaylistUseCase(
            _uiState.map { it.playlistId },
            _playlistSortState
        ).stateInVM(UiPlaylist())
    val sheetPlaylist = getSortedPlaylistUseCase(
            _uiState.map { it.sheetPlaylistId },
            _playlistSortState
        ).stateInVM(UiPlaylist())
    val albums = getAlbumsUseCase(_albumsSortState).stateInVM(emptyList())
    val artists = getArtistsUseCase(_artistsSortState).stateInVM(emptyList())
    val mostPlayedArtists = getMostPlayedArtistsUseCase().stateInVM(emptyList())

    fun onPlaybackEvent(event: PlaybackEvent) = viewModelScope.launch {
        playbackControlUseCase(event)
        if (event is PlaybackEvent.Play) sendEvent(Event.ExpandPlayer)
    }

    fun onUiEvent(event: UiEvent) = viewModelScope.launch {
        uiControlUseCase(
            event,
            {
                _sortState.update(it)
            },
            {
                _albumsSortState.update(it)
            },
            {
                _artistsSortState.update(it)
            },
            {
                _playlistsSortState.update(it)
            },
        ) {
            viewModelScope.launch {
                _uiState.value = it(_uiState.value)
            }
        }
    }

    fun onReload() = viewModelScope.launch {
        refreshUseCase()
    }

    fun onPlaylistEvent(event: PlaylistEvent) = viewModelScope.launch {
        playlistControlUseCase(event)
    }

    fun onPlaylistsEvent(event: PlaylistsUiEvent) {
        when(event) {
            PlaylistsUiEvent.HidePlaylistBottomSheet ->
                _uiState.update {
                    it.copy(playlistBottomSheetVisible = false)
                }

            is PlaylistsUiEvent.ShowBottomSheet -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(sheetPlaylistId = event.id, playlistBottomSheetVisible = true)
                    }
                }
            }

            is PlaylistsUiEvent.SetPlaylistSortState -> {
                _playlistSortState.value = event.state
            }
            is PlaylistsUiEvent.SetSortState -> {
                _playlistsSortState.value = event.state
            }
            is PlaylistsUiEvent.SetSheetVisible -> {
                _playlistsSortState.update {
                    it.copy(expanded = event.visible)
                }
            }
        }
    }

    fun onPlayerEvent(event: PlayerEvent) = viewModelScope.launch {
        when(event) {
            PlayerEvent.PlayFavorites ->
                onPlaybackEvent(PlaybackEvent.Play(favorites.first()))
            is PlayerEvent.PlayIds ->
                onPlaybackEvent(
                    PlaybackEvent.Play(getMediaByIdUseCase(event.items).first())
                )
            PlayerEvent.PlayMostPlayed ->
                onPlaybackEvent(PlaybackEvent.Play(mostPlayed.first()))
            is PlayerEvent.PlayPlaylist ->
                getPlaylistUseCase(event.id)?.let { list ->
                    onPlaybackEvent(PlaybackEvent.Play(list))
                }
            is PlayerEvent.UpdateFavorite ->
                setFavoriteUseCase(event.path, event.favorite)

            else -> {}
        }
    }

    fun setGranted(intent: StartupEvent = StartupEvent.None) {
        viewModelScope.launch(Dispatchers.IO) {
            _granted.value = true
            startupEvent = intent
            onPlaybackEvent(PlaybackEvent.Initialize)
            if (!initialized)
                refreshUseCase({
                    _uiState.update {
                        it.copy(showAppName = true)
                    }
                    delay(1500L)
                    _uiState.update {
                        it.copy(showAppName = false)
                    }
                }) {
                    when (startupEvent) {
                        StartupEvent.PlayFavorites ->
                            onPlayerEvent(PlayerEvent.PlayFavorites)

                        StartupEvent.PlayMostPlayed ->
                            onPlayerEvent(PlayerEvent.PlayMostPlayed)

                        is StartupEvent.PlayPlaylist ->
                            onPlayerEvent(
                                PlayerEvent.PlayPlaylist(
                                    (startupEvent as? StartupEvent.PlayPlaylist)?.id
                                        ?: return@refreshUseCase
                                )
                            )
                        else -> {}
                    }
                }
        }
    }

    fun onListScreenSortChange(state: SortState<SortType>) {
        _listScreenSortState.value = state
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

    fun onPlaylistSortChange(state: SortState<PlaylistSortType>) {
        _playlistSortState.value = state
    }

    fun getAlbumUi(title: String) = combine(albums, files, _listScreenSortState) { albums, files, sortState ->
        val album = albums.firstOrNull { item -> item.name == title } ?: Album()
        val files = files.filter { file -> file.album == album.name }.sortBy(sortState.sortType, sortState.ascending)
        AlbumUi(album.name, files, album.cover)
    }.stateInVM(AlbumUi())

    fun getArtistUi(name: String) = combine(artists, files, _listScreenSortState) { artists, files, sortState ->
        (artists.firstOrNull { item -> item.name == name } ?: Artist()).toArtistUi {
            it.mapNotNull { id ->
                files.firstOrNull { file -> file.id == id }
            }.sortBy(sortState.sortType, sortState.ascending)
        }
    }.stateInVM(ArtistUi())

}