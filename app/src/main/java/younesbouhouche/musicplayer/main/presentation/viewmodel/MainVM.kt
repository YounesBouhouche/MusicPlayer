package younesbouhouche.musicplayer.main.presentation.viewmodel

import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import younesbouhouche.musicplayer.core.domain.MediaPlayerService
import younesbouhouche.musicplayer.core.presentation.util.saveUriImageToInternalStorage
import younesbouhouche.musicplayer.core.presentation.util.search
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.data.db.AppDatabase
import younesbouhouche.musicplayer.main.data.models.Queue
import younesbouhouche.musicplayer.main.domain.events.FilesEvent
import younesbouhouche.musicplayer.main.domain.events.FilesEvent.AddFile
import younesbouhouche.musicplayer.main.domain.events.FilesEvent.LoadFiles
import younesbouhouche.musicplayer.main.domain.events.FilesEvent.RemoveFile
import younesbouhouche.musicplayer.main.domain.events.ListsSortEvent
import younesbouhouche.musicplayer.main.domain.events.MetadataEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistSortEvent
import younesbouhouche.musicplayer.main.domain.events.SearchEvent
import younesbouhouche.musicplayer.main.domain.events.SortEvent
import younesbouhouche.musicplayer.main.domain.events.TimerType
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.Album
import younesbouhouche.musicplayer.main.domain.models.Artist
import younesbouhouche.musicplayer.main.domain.models.ItemData
import younesbouhouche.musicplayer.main.domain.models.ListsSortType
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.models.Playlist
import younesbouhouche.musicplayer.main.presentation.states.ListSortState
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.states.PlaylistSortState
import younesbouhouche.musicplayer.main.presentation.states.PlaylistSortType
import younesbouhouche.musicplayer.main.presentation.states.PlaylistViewState
import younesbouhouche.musicplayer.main.presentation.states.SearchState
import younesbouhouche.musicplayer.main.presentation.states.SortState
import younesbouhouche.musicplayer.main.presentation.states.SortType
import younesbouhouche.musicplayer.main.presentation.states.StartupEvent
import younesbouhouche.musicplayer.main.presentation.states.UiState
import younesbouhouche.musicplayer.main.presentation.states.ViewState
import younesbouhouche.musicplayer.main.presentation.util.getMimeType
import younesbouhouche.musicplayer.main.presentation.util.toMediaItems
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainVM
    @Inject
    constructor(
        @ApplicationContext val context: Context,
        val playerDataStore: PlayerDataStore,
        db: AppDatabase,
    ) : ViewModel() {
        private val dao = db.dao
        private var startupEvent: StartupEvent = StartupEvent.None

        private val _timestamps = dao.getTimestamps()

        private fun <T> Flow<T>.stateInVM(initialValue: T) = stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), initialValue)

        private fun isFavorite(path: String) =
            dao.getFavorite(path).mapLatest { it ?: false }
                .stateInVM(false)

        private fun getTimestamps(path: String) = dao.getTimestamps(path).mapLatest { it?.times ?: emptyList() }.stateInVM(emptyList())

        private val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.READ_MEDIA_AUDIO
            } else {
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            }
        private val isGranted =
            ContextCompat
                .checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

        private val _granted = MutableStateFlow(isGranted)
        val granted = _granted.stateInVM(isGranted)

        private var initialized = false

        private val _files = MutableStateFlow(emptyList<MusicCard>())

        private fun List<Long>.toMusicCards() = mapNotNull { id -> _files.value.firstOrNull { it.id == id } }

        private val _playlists = dao.getPlaylist()
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

        private val _albums = MutableStateFlow(emptyList<Album>())
        private val _artists = MutableStateFlow(emptyList<Artist>())

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

        private val _sortState = MutableStateFlow(SortState())
        val sortState = _sortState.stateInVM(SortState())

        private val _listScreenSortState = MutableStateFlow(SortState())
        val listScreenSortState = _listScreenSortState.stateInVM(SortState())

        private val _albumsSortState = MutableStateFlow(ListSortState())
        val albumsSortState = _albumsSortState.stateInVM(ListSortState())

        private val _artistsSortState = MutableStateFlow(ListSortState())
        val artistsSortState = _artistsSortState.stateInVM(ListSortState())

        private val _playlistsSortState = MutableStateFlow(ListSortState())
        val playlistsSortState = _playlistsSortState.stateInVM(ListSortState())

        private val _playlistSortState = MutableStateFlow(PlaylistSortState())
        val playlistSortState = _playlistSortState.stateInVM(PlaylistSortState())
        private val _playlistIndex = MutableStateFlow(0)

        val lastAdded =
            _files
                .map { item -> item.sortedByDescending { it.date } }
                .stateInVM(emptyList())

        val history =
            combine(_files, _timestamps) { files, timestamps ->
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
                    }
                } else {
                    when (sortState.sortType) {
                        SortType.Title -> files.sortedByDescending { it.title }
                        SortType.Filename -> files.sortedByDescending { it.path }
                        SortType.Duration -> files.sortedByDescending { it.duration }
                        SortType.Date -> files.sortedByDescending { it.date }
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
                    }
                } else {
                    when (sortState.sortType) {
                        SortType.Title -> listScreenFiles.sortedByDescending { it.title }
                        SortType.Filename -> listScreenFiles.sortedByDescending { it.path }
                        SortType.Duration -> listScreenFiles.sortedByDescending { it.duration }
                        SortType.Date -> listScreenFiles.sortedByDescending { it.date }
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
                    }
                } else {
                    when (sortState.sortType) {
                        SortType.Title -> listScreenFiles.sortedByDescending { it.title }
                        SortType.Filename -> listScreenFiles.sortedByDescending { it.path }
                        SortType.Duration -> listScreenFiles.sortedByDescending { it.duration }
                        SortType.Date -> listScreenFiles.sortedByDescending { it.date }
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

        val playlist =
            combine(_playlistsSorted, _playlistIndex) { playlists, index ->
                playlists.getOrNull(index) ?: Playlist()
            }.stateInVM(Playlist())

        val bottomSheetPlaylist =
            combine(_playlistsSorted, _playlistIndex) { playlists, index ->
                playlists.getOrNull(index) ?: Playlist()
            }.stateInVM(Playlist())

        val playlistFiles =
            combine(
                _playlistsSorted,
                _files,
                _playlistSortState,
                _playlistIndex,
            ) { playlists, files, sortState, index ->
                val list =
                    playlists.getOrNull(index)?.items?.mapNotNull { item -> files.firstOrNull { it.path == item } }
                        ?: emptyList()
                if (sortState.ascending) {
                    when (sortState.sortType) {
                        PlaylistSortType.Custom -> list
                        PlaylistSortType.Title -> list.sortedBy { it.title }
                        PlaylistSortType.Duration -> list.sortedBy { it.duration }
                        PlaylistSortType.Filename -> list.sortedBy { it.path }
                    }
                } else {
                    when (sortState.sortType) {
                        PlaylistSortType.Custom -> list.reversed()
                        PlaylistSortType.Title -> list.sortedByDescending { it.title }
                        PlaylistSortType.Duration -> list.sortedByDescending { it.duration }
                        PlaylistSortType.Filename -> list.sortedByDescending { it.path }
                    }
                }
            }.stateInVM(emptyList())

        val bottomSheetPlaylistFiles =
            combine(_playlistsSorted, _files, _playlistIndex) { playlists, files, index ->
                playlists.getOrNull(index)?.items?.mapNotNull { item -> files.firstOrNull { it.path == item } }
                    ?: emptyList()
            }.stateInVM(emptyList())

        val playlistsSorted =
            _playlistsSorted
                .stateInVM(emptyList())

        private val rememberRepeat = playerDataStore.rememberRepeat

        private val rememberShuffle = playerDataStore.rememberShuffle

        private val rememberSpeed = playerDataStore.rememberSpeed

        private val rememberPitch = playerDataStore.rememberPitch

        private val repeatMode = playerDataStore.repeatMode

        private val shuffle = playerDataStore.shuffle

        private val speed = playerDataStore.speed

        private val pitch = playerDataStore.pitch

        fun getAlbum(file: MusicCard): Album? = _albums.value.firstOrNull { it.title == file.album }

        fun getArtist(file: MusicCard): Artist? = _artists.value.firstOrNull { it.name == file.artist }

        fun setGranted(intent: StartupEvent = StartupEvent.None) {
            viewModelScope.launch {
                _granted.value = true
                if (!initialized) loadFiles()
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

        fun onFilesEvent(event: FilesEvent) {
            when (event) {
                LoadFiles -> {
                    viewModelScope.launch {
                        loadFiles()
                    }
                }

                is AddFile -> {
                    viewModelScope.launch {
                        _files.value += event.file
                    }
                }

                is RemoveFile -> {
                    viewModelScope.launch {
                        _files.value -= event.file
                    }
                }

                is FilesEvent.UpdateMetadata -> {
                    if (Environment.isExternalStorageManager()) {
                        with(AudioFileIO.read(File(event.metadata.path))) {
                            tag.setField(FieldKey.TITLE, event.metadata.newTitle)
                            tag.setField(FieldKey.ARTIST, event.metadata.newArtist)
                            tag.setField(FieldKey.ALBUM, event.metadata.newAlbum)
                            tag.setField(FieldKey.YEAR, event.metadata.newYear)
                            tag.setField(FieldKey.COMPOSER, event.metadata.newComposer)
                            tag.setField(FieldKey.GENRE, event.metadata.newGenre)
                            AudioFileIO.write(this)
                        }
                        event.metadata.uri.getMimeType(context.contentResolver)?.run {
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(event.metadata.path),
                                arrayOf(this),
                            ) { _, _ -> }
                        }
                        viewModelScope.launch {
                            loadFiles()
                        }
                    } else {
                        val editPendingIntent =
                            MediaStore.createWriteRequest(
                                context.contentResolver,
                                listOf(event.metadata.uri),
                            )
                        context.startIntentSender(editPendingIntent.intentSender, null, 0, 0, 0)
                    }
                }
            }
        }

        private fun onItemsSortEvent(
            event: SortEvent,
            state: MutableStateFlow<SortState>,
        ) {
            when (event) {
                SortEvent.Collapse ->
                    state.update {
                        it.copy(expanded = false)
                    }

                SortEvent.Expand ->
                    state.update {
                        it.copy(expanded = true)
                    }

                SortEvent.ToggleAscending ->
                    state.update {
                        it.copy(ascending = !it.ascending)
                    }

                is SortEvent.UpdateAscending ->
                    state.update {
                        it.copy(ascending = event.ascending)
                    }

                is SortEvent.UpdateExpanded ->
                    state.update {
                        it.copy(expanded = event.expanded)
                    }

                is SortEvent.UpdateSortType ->
                    state.update {
                        it.copy(sortType = event.sortType)
                    }

                is SortEvent.UpdateSortTypeOrToggleAsc -> {
                    state.update {
                        if (it.sortType == event.sortType) {
                            it.copy(ascending = !it.ascending)
                        } else {
                            it.copy(sortType = event.sortType)
                        }
                    }
                }
            }
        }

        fun onSortEvent(event: SortEvent) = onItemsSortEvent(event, _sortState)

        fun onListSortEvent(event: SortEvent) = onItemsSortEvent(event, _listScreenSortState)

        fun onPlaylistSortEvent(event: PlaylistSortEvent) {
            when (event) {
                PlaylistSortEvent.Collapse -> {
                    _playlistSortState.update {
                        it.copy(expanded = false)
                    }
                }

                PlaylistSortEvent.Expand -> {
                    _playlistSortState.update {
                        it.copy(expanded = true)
                    }
                }

                PlaylistSortEvent.ToggleAscending -> {
                    _playlistSortState.update {
                        it.copy(ascending = !it.ascending)
                    }
                }

                is PlaylistSortEvent.UpdateAscending -> {
                    _playlistSortState.update {
                        it.copy(ascending = event.ascending)
                    }
                }

                is PlaylistSortEvent.UpdateExpanded -> {
                    _playlistSortState.update {
                        it.copy(expanded = event.expanded)
                    }
                }

                is PlaylistSortEvent.UpdateSortType -> {
                    _playlistSortState.update {
                        it.copy(sortType = event.sortType)
                    }
                }

                is PlaylistSortEvent.UpdateSortTypeOrToggleAsc -> {
                    _playlistSortState.update {
                        if (it.sortType == event.sortType) {
                            it.copy(ascending = !it.ascending)
                        } else {
                            it.copy(sortType = event.sortType)
                        }
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

        private fun onListsSortEvent(
            event: ListsSortEvent,
            state: MutableStateFlow<ListSortState>,
        ) {
            when (event) {
                ListsSortEvent.Collapse -> {
                    state.update {
                        it.copy(expanded = false)
                    }
                }

                ListsSortEvent.Expand -> {
                    state.update {
                        it.copy(expanded = true)
                    }
                }

                ListsSortEvent.ToggleAscending -> {
                    state.update {
                        it.copy(ascending = !it.ascending)
                    }
                }

                is ListsSortEvent.UpdateAscending -> {
                    state.update {
                        it.copy(ascending = event.ascending)
                    }
                }

                is ListsSortEvent.UpdateExpanded -> {
                    state.update {
                        it.copy(expanded = event.expanded)
                    }
                }

                is ListsSortEvent.UpdateSortType -> {
                    state.update {
                        it.copy(sortType = event.sortType)
                    }
                }

                is ListsSortEvent.UpdateSortTypeOrToggleAsc -> {
                    state.update {
                        if (it.sortType == event.sortType) {
                            it.copy(ascending = !it.ascending)
                        } else {
                            it.copy(sortType = event.sortType)
                        }
                    }
                }

                ListsSortEvent.CollapseCols -> {
                    state.update {
                        it.copy(colsExpanded = false)
                    }
                }

                ListsSortEvent.ExpandCols -> {
                    state.update {
                        it.copy(colsExpanded = true)
                    }
                }

                is ListsSortEvent.UpdateColsCount -> {
                    state.update {
                        it.copy(colsCount = event.colsCount)
                    }
                }

                is ListsSortEvent.UpdateColsCountExpanded -> {
                    state.update {
                        it.copy(colsExpanded = event.expanded)
                    }
                }
            }
        }

        fun onAlbumsSortEvent(event: ListsSortEvent) {
            onListsSortEvent(event, _albumsSortState)
        }

        fun onArtistsSortEvent(event: ListsSortEvent) {
            onListsSortEvent(event, _artistsSortState)
        }

        fun onPlaylistsSortEvent(event: ListsSortEvent) {
            onListsSortEvent(event, _playlistsSortState)
        }

        private suspend fun loadFiles() {
            _files.value = emptyList()
            _albums.value = emptyList()
            _artists.value = emptyList()
            _uiState.update {
                it.copy(loading = true)
            }
            val list = mutableListOf<MusicCard>()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val cursor =
                        context.contentResolver.query(
                            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
                            arrayOf(
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Media.DISPLAY_NAME,
                                MediaStore.Audio.Media.DURATION,
                                MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.ALBUM_ID,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media.DATA,
                            ),
                            MediaStore.Audio.Media.IS_MUSIC + "!= 0",
                            null,
                            MediaStore.Audio.Media.IS_MUSIC + "!= 0",
                        )
                    cursor?.use { crs ->
                        val idColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                        val durationColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                        val titleColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                        val artistColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                        val albumIdColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                        val albumColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                        val pathColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                        while (crs.moveToNext()) {
                            val id = crs.getLong(idColumn)
                            val duration = crs.getLong(durationColumn)
                            val title = crs.getString(titleColumn)
                            val artist = crs.getString(artistColumn)
                            val albumId = crs.getLong(albumIdColumn)
                            val album = crs.getString(albumColumn)
                            val path = crs.getString(pathColumn)
                            val date = Files.getLastModifiedTime(Paths.get(path)).toMillis()
                            val contentUri: Uri =
                                ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    id,
                                )
                            list.add(
                                MusicCard
                                    .Builder()
                                    .setContentUri(contentUri)
                                    .setId(id)
                                    .setTitle(title)
                                    .setArtist(artist)
                                    .setAlbum(album)
                                    .setAlbumId(albumId)
                                    .setPath(path)
                                    .setDate(date)
                                    .setDuration(duration)
                                    .setFavorite(isFavorite(path))
                                    .setTimestamps(getTimestamps(path))
                                    .build(),
                            )
                        }
                        crs.close()
                        initialized = true
                    }
                }
                _albums.value =
                    list.groupBy { it.album }.map { album ->
                        Album(
                            title = album.key,
                            items = album.value.map { it.id },
                            cover = null,
                        )
                    }
                // do the same thing for _artists
                _artists.value =
                    list.groupBy { it.artist }.map { artist ->
                        Artist(
                            name = artist.key,
                            items = artist.value.map { it.id },
                            cover = null,
                        )
                    }
                _files.value = list
                _uiState.update {
                    it.copy(loading = false)
                }
                withContext(Dispatchers.IO) {
                    list.forEachIndexed { index, file ->
                        try {
                            AudioFileIO.read(File(file.path)).tag.let {
                                file.lyrics = it.getFirst(FieldKey.LYRICS)
                                file.genre = it.getFirst(FieldKey.GENRE)
                                file.composer = it.getFirst(FieldKey.COMPOSER)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val result =
                            with(MediaMetadataRetriever()) {
                                try {
                                    setDataSource(context, file.contentUri)
                                    if (embeddedPicture != null) {
                                        BitmapFactory.decodeByteArray(
                                            embeddedPicture,
                                            0,
                                            embeddedPicture!!.size,
                                        )!! to embeddedPicture!!
                                    } else {
                                        null
                                    }
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        list[index].apply {
                            result?.let { r ->
                                cover = r.first
                                coverByteArray = r.second
                                _albums.update { list ->
                                    list.toMutableList().apply {
                                        if (firstOrNull { it.title == file.album }?.cover == null) {
                                            firstOrNull { it.title == file.album }?.cover = r.first
                                        }
                                    }.toList()
                                }
                                _artists.update { list ->
                                    list.toMutableList().apply {
                                        if (firstOrNull { it.name == file.artist }?.cover == null) {
                                            firstOrNull { it.name == file.artist }?.cover = r.first
                                        }
                                    }.toList()
                                }
                            }
                        }
                    }
                    _files.value = list
                }
            }
        }

        private var controllerFuture: ListenableFuture<MediaController>
        private lateinit var player: Player
        private val observer =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    _playerState.update {
                        it.copy(volume = getVolume())
                    }
                }
            }

        protected fun finalize() {
            context.contentResolver.unregisterContentObserver(observer)
        }

        init {
            context.contentResolver.registerContentObserver(
                Settings.System.CONTENT_URI,
                true,
                observer,
            )
            val sessionToken =
                SessionToken(context, ComponentName(context, MediaPlayerService::class.java))
            context.startForegroundService(Intent(context, MediaSessionService::class.java))
            controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture.addListener({
                player = controllerFuture.get()
                viewModelScope.launch {
                    if (player.playWhenReady) {
                        dao.updateCurrentIndex(player.currentMediaItemIndex)
                        _playerState.update {
                            it.copy(
                                time = player.currentPosition,
                                playState =
                                    if (player.isPlaying) PlayState.PLAYING else PlayState.PAUSED,
                                repeatMode = player.repeatMode,
                                shuffle = player.shuffleModeEnabled,
                                speed = player.playbackParameters.speed,
                                pitch = player.playbackParameters.pitch,
                            )
                        }
                        dao.updateCurrentIndex(player.currentMediaItemIndex)
                    }
                    while (true) {
                        _playerState.update {
                            it.copy(time = player.currentPosition)
                        }
                        delay(100L)
                    }
                }
                player.addListener(
                    object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            _playerState.update {
                                it.copy(loading = playbackState == Player.STATE_BUFFERING)
                            }
                        }

                        override fun onRepeatModeChanged(mode: Int) {
                            super.onRepeatModeChanged(mode)
                            viewModelScope.launch {
                                _playerState.update {
                                    it.copy(
                                        repeatMode = mode,
                                        hasNextItem = player.hasNextMediaItem(),
                                        hasPrevItem = player.hasPreviousMediaItem(),
                                    )
                                }
                                playerDataStore.override(
                                    repeatMode = mode,
                                )
                            }
                        }

                        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                            viewModelScope.launch {
                                _playerState.update {
                                    it.copy(shuffle = shuffleModeEnabled)
                                }
                                playerDataStore.override(
                                    shuffle = shuffleModeEnabled,
                                )
                            }
                        }

                        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                            super.onPlaybackParametersChanged(playbackParameters)
                            viewModelScope.launch {
                                _playerState.update {
                                    it.copy(speed = playbackParameters.speed)
                                }
                                playerDataStore.override(
                                    speed = playbackParameters.speed,
                                )
                            }
                        }

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            if (isPlaying) {
                                _playerState.update { it.copy(playState = PlayState.PLAYING) }
                            } else {
                                _playerState.update { it.copy(playState = PlayState.PAUSED) }
                            }
                        }

                        override fun onMediaItemTransition(
                            mediaItem: MediaItem?,
                            reason: Int,
                        ) {
                            super.onMediaItemTransition(mediaItem, reason)
                            viewModelScope.launch {
                                dao.updateCurrentIndex(player.currentMediaItemIndex)
                                try {
                                    dao.addTimestamp(queueFiles.value[player.currentMediaItemIndex].path)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            _playerState.update {
                                it.copy(
                                    hasNextItem = player.hasNextMediaItem(),
                                    hasPrevItem = player.hasPreviousMediaItem(),
                                )
                            }
                            if (
                                (_playerState.value.timer is TimerType.End) and
                                (
                                    (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) or
                                        (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
                                )
                            ) {
                                if ((_playerState.value.timer as TimerType.End).tracks > 1) {
                                    _playerState.update {
                                        val timer = (it.timer as TimerType.End)
                                        it.copy(timer = timer.copy(tracks = timer.tracks - 1))
                                    }
                                } else {
                                    onPlayerEvent(PlayerEvent.Stop)
                                }
                            }
                        }
                    },
                )
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
            }, ContextCompat.getMainExecutor(context))
        }

        private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        private val _playerState =
            MutableStateFlow(
                PlayerState(
                    volume = getVolume(),
                ),
            )

        val playerState = _playerState.stateInVM(PlayerState())

        private val _uiState = MutableStateFlow(UiState())
        val uiState =
            combine(_uiState, playerDataStore.settings) { uiState, settings ->
                uiState.copy(showVolumeSlider = settings.first, showPitch = settings.second)
            }.stateInVM(UiState())

        private var timerTask = Task(viewModelScope)

        fun getVolume() =
            (
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() /
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            )

        fun onPlayerEvent(event: PlayerEvent) {
            when (event) {
                is PlayerEvent.Backward -> player.seekTo(player.currentPosition - event.ms)
                is PlayerEvent.Forward -> player.seekTo(player.currentPosition + event.ms)
                PlayerEvent.Next -> player.seekToNext()
                PlayerEvent.Pause -> player.pause()
                PlayerEvent.PauseResume -> {
                    if (player.isPlaying) {
                        player.pause()
                    } else {
                        player.play()
                    }
                }

                is PlayerEvent.Play -> play(event.items, event.index)
                is PlayerEvent.PlayPaths ->
                    play(
                        event.items.mapNotNull { item -> _files.value.firstOrNull { it.path == item } },
                        event.index,
                    )

                is PlayerEvent.PlayIds ->
                    play(
                        event.items.mapNotNull { item -> _files.value.firstOrNull { it.id == item } },
                        event.index,
                    )

                PlayerEvent.Previous -> player.seekToPrevious()

                PlayerEvent.Resume -> player.play()

                is PlayerEvent.Seek ->
                    if (!((event.skipIfSameIndex) and (event.index == queueIndex.value))) {
                        player.seekTo(event.index, event.time)
                    }

                is PlayerEvent.SeekTime -> player.seekTo(event.time)

                PlayerEvent.Stop -> {
                    timeTask.stop()
                    player.stop()
                    player.clearMediaItems()
                    viewModelScope.launch {
                        dao.upsertQueue(Queue())
                        _playerState.update {
                            it.copy(
                                playState = PlayState.STOP,
                                timer = TimerType.Disabled,
                            )
                        }
                        _uiState.update {
                            it.copy(
                                viewState = ViewState.HIDDEN,
                            )
                        }
                    }
                }

                is PlayerEvent.Remove -> {
                    player.removeMediaItem(event.index)
                    viewModelScope.launch(Dispatchers.IO) {
                        dao.updateQueue(
                            _queueList.value.toMutableList().apply {
                                removeAt(event.index)
                            },
                        )
                    }
                }

                is PlayerEvent.Swap -> {
                    player.moveMediaItem(event.from, event.to)
                    val index = player.currentMediaItemIndex
                    viewModelScope.launch(Dispatchers.IO) {
                        dao.upsertQueue(
                            Queue(
                                items =
                                    _queueList.value.toMutableList().apply {
                                        add(event.to, removeAt(event.from))
                                    },
                                index = index,
                            ),
                        )
                    }
                }

                PlayerEvent.CycleRepeatMode -> {
                    onPlayerEvent(
                        PlayerEvent.SetRepeatMode(
                            when (player.repeatMode) {
                                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                                else -> Player.REPEAT_MODE_OFF
                            },
                        ),
                    )
                }

                is PlayerEvent.SetRepeatMode -> {
                    player.repeatMode = event.repeatMode
                }

                PlayerEvent.ToggleShuffle -> {
                    player.shuffleModeEnabled = !player.shuffleModeEnabled
                }

                PlayerEvent.ResetSpeed -> player.setPlaybackSpeed(1f)
                is PlayerEvent.SetSpeed -> player.setPlaybackSpeed(event.speed)
                is PlayerEvent.SetTimer -> {
                    event.timer.let {
                        _playerState.update { state ->
                            state.copy(timer = it)
                        }
                        if (it is TimerType.Disabled) {
                            timerTask.stop()
                        } else {
                            timerTask.start {
                                when (it) {
                                    is TimerType.Duration -> {
                                        while ((_playerState.value.timer as TimerType.Duration).ms > 0) {
                                            delay(1000L)
                                            _playerState.update { state ->
                                                state.copy(
                                                    timer =
                                                        TimerType.Duration(
                                                            (state.timer as TimerType.Duration).ms - 1000L,
                                                        ),
                                                )
                                            }
                                        }
                                        onPlayerEvent(PlayerEvent.Stop)
                                    }
                                    is TimerType.Time -> {
                                        while (true) {
                                            val time = with(LocalDateTime.now()) { hour * 60 + minute }
                                            if (abs(time - (it.hour * 60 + it.min)) == 0) break
                                            delay(1000L)
                                        }
                                        onPlayerEvent(PlayerEvent.Stop)
                                    }
                                    else -> return@start
                                }
                            }
                        }
                    }
                }

                is PlayerEvent.UpdateFavorite -> {
                    viewModelScope.launch {
                        dao.upsertItem(ItemData(event.path, event.favorite))
                    }
                }

                is PlayerEvent.SetFavorite -> {
                    viewModelScope.launch {
                        dao.upsertItem(ItemData(event.path, true))
                    }
                }

                is PlayerEvent.ToggleFavorite -> {
                    viewModelScope.launch {
                        dao.upsertItem(ItemData(event.path, !isFavorite(event.path).first()))
                    }
                }

                is PlayerEvent.AddToNext -> {
                    if (_playerState.value.playState == PlayState.STOP) {
                        play(event.items, autoPlay = false)
                        return
                    }
                    event.items
                        .map { _queueList.value.indexOf(it.id) }
                        .filter { it >= 0 }
                        .forEach { player.removeMediaItem(it) }
                    player.addMediaItems(
                        player.currentMediaItemIndex + 1,
                        event.items.toMediaItems(),
                    )
                    val items = event.items.map { it.id }
                    viewModelScope.launch {
                        dao.upsertQueue(
                            Queue(
                                items =
                                    _queueList.value.toMutableList().apply {
                                        removeAll(items)
                                        addAll(player.currentMediaItemIndex + 1, items)
                                    },
                                index = player.currentMediaItemIndex,
                            ),
                        )
                    }
                }

                is PlayerEvent.AddToQueue -> {
                    if (_playerState.value.playState == PlayState.STOP) {
                        play(event.items, autoPlay = false)
                        return
                    }
                    val list =
                        event.items.filter { item ->
                            !_queueList.value.any { it == item.id }
                        }
                    viewModelScope.launch {
                        dao.updateQueue(
                            _queueList.value.toMutableList().apply { addAll(list.map { it.id }) },
                        )
                    }
                    player.addMediaItems(list.toMediaItems())
                }

                PlayerEvent.PlayFavorites ->
                    viewModelScope.launch {
                        play(favoritesFiles.value)
                    }

                PlayerEvent.PlayMostPlayed ->
                    viewModelScope.launch {
                        play(mostPlayed.value)
                    }

                is PlayerEvent.PlayPlaylist ->
                    viewModelScope.launch {
                        val p =
                            playlists.value
                                .firstOrNull { it.id == event.id }
                                ?.items
                                ?.mapNotNull { item -> _files.value.firstOrNull { item == it.path } }
                        println(p)
                        p?.let {
                            play(it)
                        }
                    }

                PlayerEvent.DecreaseVolume -> {
                    audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        0,
                    )
                    _playerState.update {
                        it.copy(volume = getVolume())
                    }
                }

                PlayerEvent.IncreaseVolume -> {
                    audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        0,
                    )
                    _playerState.update {
                        it.copy(volume = getVolume())
                    }
                }

                is PlayerEvent.SetVolume -> {
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        (event.volume * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                            .roundToInt(),
                        0,
                    )
                    _playerState.update {
                        it.copy(volume = getVolume())
                    }
                }

                is PlayerEvent.SetPitch ->
                    event.pitch.let {
                        player.playbackParameters =
                            PlaybackParameters(
                                player.playbackParameters.speed,
                                it,
                            )
                        _playerState.update { state ->
                            state.copy(pitch = it)
                        }
                        viewModelScope.launch {
                            playerDataStore.override(pitch = it)
                        }
                    }

                is PlayerEvent.SetPlayerVolume -> {
                    if (_playerState.value.playState != PlayState.STOP) {
                        player.volume = event.volume
                    }
                }
            }
        }

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

        fun onPlaylistEvent(event: PlaylistEvent) {
            when (event) {
                is PlaylistEvent.AddToPlaylist -> {
                    viewModelScope.launch {
                        with(_playlists.first()[_uiState.value.addToPlaylistIndex]) {
                            dao.upsertPlaylist(
                                copy(
                                    items =
                                        items +
                                            _uiState.value.addToPlaylistItems.filter { item ->
                                                !items.any { it == item }
                                            },
                                ),
                            )
                        }
                    }
                }

                is PlaylistEvent.CreateNewPlaylist -> {
                    viewModelScope.launch {
                        dao.upsertPlaylist(
                            Playlist(
                                name = event.name,
                                items = event.items,
                            ),
                        )
                    }
                }

                PlaylistEvent.CreateNew -> {
                    viewModelScope.launch {
                        val fileName = "pl_${playlists.value.size}.jpg"
                        val saved =
                            uiState.value.newPlaylistImage?.let {
                                withContext(Dispatchers.IO) {
                                    saveUriImageToInternalStorage(context, it, fileName) != null
                                }
                            } ?: false
                        dao.upsertPlaylist(
                            Playlist(
                                name = uiState.value.newPlaylistName,
                                items = uiState.value.newPlaylistItems,
                                image = if (saved) fileName else null,
                            ),
                        )
                    }
                }

                is PlaylistEvent.Reorder ->
                    viewModelScope.launch {
                        with(event.playlist) {
                            dao.upsertPlaylist(
                                copy(
                                    items =
                                        items.toMutableList().apply {
                                            add(event.to, removeAt(event.from))
                                        },
                                ),
                            )
                        }
                    }

                is PlaylistEvent.RemoveAt ->
                    viewModelScope.launch {
                        with(event.playlist) {
                            dao.upsertPlaylist(
                                copy(
                                    items =
                                        items.toMutableList().apply {
                                            removeAt(event.index)
                                        },
                                ),
                            )
                        }
                    }

                is PlaylistEvent.DeletePlaylist -> {
                    _uiState.update {
                        it.copy(playlistBottomSheetVisible = false)
                    }
                    viewModelScope.launch {
                        dao.deletePlaylist(event.playlist)
                    }
                }

                is PlaylistEvent.RenamePlaylist ->
                    viewModelScope.launch {
                        dao.updatePlaylistName(
                            uiState.value.renamePlaylistId,
                            uiState.value.renamePlaylistName,
                        )
                    }
            }
        }

        private var timeTask = Task(viewModelScope)

        private fun startTimeUpdate() =
            timeTask.startRepeating(100L) {
                _playerState.update {
                    it.copy(time = player.currentPosition)
                }
            }

        private fun play(
            list: List<MusicCard>,
            index: Int = 0,
            time: Long = 0L,
            autoPlay: Boolean = true,
        ) {
            if (list.isEmpty()) return
            if ((_queueList.value == list) and (_playerState.value.playState != PlayState.STOP)) {
                if (index != queueIndex.value) seek(index, time)
                return
            }
            viewModelScope.launch(Dispatchers.IO) {
                dao.upsertQueue(Queue(items = list.map { it.id }, index = index))
            }
            player.setMediaItems(list.toMediaItems())
            player.seekTo(index, time)
            player.prepare()
            if (autoPlay) player.play()
            viewModelScope.launch {
                player.repeatMode =
                    if (rememberRepeat.first()) {
                        repeatMode.first()
                    } else {
                        Player.REPEAT_MODE_OFF
                    }
                player.shuffleModeEnabled =
                    if (rememberShuffle.first()) shuffle.first() else false
                player.playbackParameters =
                    PlaybackParameters(
                        if (rememberSpeed.first()) {
                            speed.first()
                        } else {
                            1f
                        },
                        if (rememberPitch.first()) {
                            pitch.first()
                        } else {
                            1f
                        },
                    )
                dao.addTimestamp(list[index].path)
                dao.updateCurrentIndex(index)
                _playerState.update {
                    it.copy(
                        time = time,
                        playState = if (autoPlay) PlayState.PLAYING else PlayState.PAUSED,
                        pitch = pitch.first(),
                    )
                }
                onUiEvent(UiEvent.SetViewState(ViewState.SMALL))
                startTimeUpdate()
            }
        }

        private fun seek(
            index: Int,
            time: Long,
        ) {
            viewModelScope.launch {
                dao.updateCurrentIndex(index)
            }
            _playerState.update {
                it.copy(time = time)
            }
            player.seekTo(index, time)
        }

        // create a string list extension function that returns corresponding files
    }
