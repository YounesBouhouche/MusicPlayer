package younesbouhouche.musicplayer.viewmodel

import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import younesbouhouche.musicplayer.events.FilesEvent
import younesbouhouche.musicplayer.events.FilesEvent.AddFile
import younesbouhouche.musicplayer.events.FilesEvent.LoadFiles
import younesbouhouche.musicplayer.events.FilesEvent.RemoveFile
import younesbouhouche.musicplayer.events.ListsSortEvent
import younesbouhouche.musicplayer.events.MetadataEvent
import younesbouhouche.musicplayer.events.PlayerEvent
import younesbouhouche.musicplayer.events.PlaylistEvent
import younesbouhouche.musicplayer.events.PlaylistSortEvent
import younesbouhouche.musicplayer.events.SearchEvent
import younesbouhouche.musicplayer.events.SortEvent
import younesbouhouche.musicplayer.events.TimerType
import younesbouhouche.musicplayer.events.UiEvent
import younesbouhouche.musicplayer.getMimeType
import younesbouhouche.musicplayer.models.Album
import younesbouhouche.musicplayer.models.Artist
import younesbouhouche.musicplayer.models.MusicCard
import younesbouhouche.musicplayer.models.Playlist
import younesbouhouche.musicplayer.room.AppDatabase
import younesbouhouche.musicplayer.room.ItemData
import younesbouhouche.musicplayer.room.Timestamp
import younesbouhouche.musicplayer.search
import younesbouhouche.musicplayer.services.MediaPlayerService
import younesbouhouche.musicplayer.states.ListSortState
import younesbouhouche.musicplayer.states.ListsSortType
import younesbouhouche.musicplayer.states.PlayState
import younesbouhouche.musicplayer.states.PlayerState
import younesbouhouche.musicplayer.states.PlaylistSortState
import younesbouhouche.musicplayer.states.PlaylistSortType
import younesbouhouche.musicplayer.states.PlaylistViewState
import younesbouhouche.musicplayer.states.SearchState
import younesbouhouche.musicplayer.states.SortState
import younesbouhouche.musicplayer.states.SortType
import younesbouhouche.musicplayer.states.StartupEvent
import younesbouhouche.musicplayer.states.UiState
import younesbouhouche.musicplayer.states.ViewState
import younesbouhouche.musicplayer.toMediaItems
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Named
import kotlin.io.path.Path
import kotlin.math.abs

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainVM @Inject constructor(
    @ApplicationContext val context: Context,
    @Named("collection") val collection: Uri,
    @Named("projection") val projection: Array<String>,
    @Named("selection") val selection: String,
    @Named("sortOrder") val sortOrder: String,
    db: AppDatabase
): ViewModel() {
    private val dao = db.dao
    private var startupEvent: StartupEvent = StartupEvent.None

    private val _timestamps = dao.getGroupedTimestamps().map { timestamp ->
        timestamp.filter { Files.exists(Path(it.key)) }
    }

    private val _loading = MutableStateFlow(false)
    val loading = _loading.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private fun isFavorite(path: String) = dao.getFavorite(path).mapLatest { it ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private fun getTimestamps(path: String) = dao.getTimestamps(path).mapLatest { it?.times ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            android.Manifest.permission.READ_MEDIA_AUDIO
        else
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val isGranted = ContextCompat
        .checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private val _granted = MutableStateFlow(isGranted)
    val granted = _granted.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), isGranted)

    private var initialized = false

    private val _files = MutableStateFlow(emptyList<MusicCard>())

    private fun List<Long>.toMusicCards() = mapNotNull { id -> _files.value.firstOrNull { it.id == id } }

    private val _playlists = dao.getPlaylist()
    val playlists = _playlists.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _nowPlaying = MutableStateFlow(emptyList<Long>())
    val queue = combine(_files, _nowPlaying) { files, nowPlaying ->
        nowPlaying.mapNotNull { item -> files.firstOrNull { it.id == item } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _listScreenFiles = MutableStateFlow(emptyList<MusicCard>())

    private val _albums = MutableStateFlow(emptyList<Album>())
    private val _artists = MutableStateFlow(emptyList<Artist>())

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = combine(_searchState, _files) { state, files ->
        val results =
            if (state.query.isNotBlank())
                files.filter { it.search(state.query) }
            else emptyList()
        state.copy(result = results)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SearchState())

    private val _sortState = MutableStateFlow(SortState())
    val sortState = _sortState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SortState())

    private val _listScreenSortState = MutableStateFlow(SortState())
    val listScreenSortState = _listScreenSortState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SortState())

    private val _albumsSortState = MutableStateFlow(ListSortState())
    val albumsSortState = _albumsSortState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ListSortState())

    private val _artistsSortState = MutableStateFlow(ListSortState())
    val artistsSortState = _artistsSortState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ListSortState())

    private val _playlistsSortState = MutableStateFlow(ListSortState())
    val playlistsSortState = _playlistsSortState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ListSortState())

    private val _playlistSortState = MutableStateFlow(PlaylistSortState())
    val playlistSortState = _playlistSortState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PlaylistSortState())
    private val _playlistIndex = MutableStateFlow(0)

    val recentlyAdded = _files
        .mapLatest { list -> list.sortedByDescending { it.date } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val recentlyPlayed = combine(_files, _timestamps) { files, timestamps ->
        files
            .asSequence()
            .map { it to timestamps[it.path]?.maxOrNull() }
            .filter { it.second != null }
            //.sortedByDescending { it.second }
            .map { it.first }
            .toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val filesSorted = combine(_files, _sortState) { files, sortState ->
        if (sortState.ascending)
            when(sortState.sortType) {
                SortType.Title -> files.sortedBy { it.title }
                SortType.Filename -> files.sortedBy { it.path }
                SortType.Duration -> files.sortedBy { it.duration }
                SortType.Date -> files.sortedBy { it.date }
            }
        else
            when(sortState.sortType) {
                SortType.Title -> files.sortedByDescending { it.title }
                SortType.Filename -> files.sortedByDescending { it.path }
                SortType.Duration -> files.sortedByDescending { it.duration }
                SortType.Date -> files.sortedByDescending { it.date }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val timestampsCards = combine(_files, _timestamps) { files, timestamps ->
        files
            .filter {
                timestamps.containsKey(it.path)
            }.associateWith {
                timestamps[it.path]!!
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyMap())

    private val _mostPlayedArtists = combine(_artists, _timestamps) { artists, timestamps ->
        // get most played artists
        artists
            .filter { it.name != "<unknown>" }
            .map { artist ->
                artist to artist.items.toMusicCards().sumOf { item -> timestamps[item.path]?.size ?: 0 }
            }
            .sortedByDescending { it.second }
            .map { it.first }
    }
    val mostPlayedArtists = _mostPlayedArtists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val listScreenFiles = combine(_listScreenFiles, _listScreenSortState) { listScreenFiles, sortState ->
        if (sortState.ascending)
            when(sortState.sortType) {
                SortType.Title -> listScreenFiles.sortedBy { it.title }
                SortType.Filename -> listScreenFiles.sortedBy { it.path }
                SortType.Duration -> listScreenFiles.sortedBy { it.duration }
                SortType.Date -> listScreenFiles.sortedBy { it.date }
            }
        else
            when(sortState.sortType) {
                SortType.Title -> listScreenFiles.sortedByDescending { it.title }
                SortType.Filename -> listScreenFiles.sortedByDescending { it.path }
                SortType.Duration -> listScreenFiles.sortedByDescending { it.duration }
                SortType.Date -> listScreenFiles.sortedByDescending { it.date }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _favorites = dao.getFavorites()
    private val _favoritesFiles = combine(_files, _favorites) { files, favorites ->
        files.filter { favorites.contains(it.path) }
    }
    val favoritesFiles = combine(_favoritesFiles, _listScreenSortState) { listScreenFiles, sortState ->
        if (sortState.ascending)
            when(sortState.sortType) {
                SortType.Title -> listScreenFiles.sortedBy { it.title }
                SortType.Filename -> listScreenFiles.sortedBy { it.path }
                SortType.Duration -> listScreenFiles.sortedBy { it.duration }
                SortType.Date -> listScreenFiles.sortedBy { it.date }
            }
        else
            when(sortState.sortType) {
                SortType.Title -> listScreenFiles.sortedByDescending { it.title }
                SortType.Filename -> listScreenFiles.sortedByDescending { it.path }
                SortType.Duration -> listScreenFiles.sortedByDescending { it.duration }
                SortType.Date -> listScreenFiles.sortedByDescending { it.date }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val albumsSorted = combine(_albums, _albumsSortState) { albums, sortState ->
        if (sortState.ascending)
            when(sortState.sortType) {
                ListsSortType.Name -> albums.toList().sortedBy { it.title }
                ListsSortType.Count -> albums.toList().sortedBy { it.items.size }
            }
        else
            when(sortState.sortType) {
                ListsSortType.Name -> albums.toList().sortedByDescending { it.title }
                ListsSortType.Count -> albums.toList().sortedByDescending { it.items.size }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val artistsSorted = combine(_artists, _artistsSortState) { artists, sortState ->
        if (sortState.ascending)
            when(sortState.sortType) {
                ListsSortType.Name -> artists.toList().sortedBy { it.name }
                ListsSortType.Count -> artists.toList().sortedBy { it.items.size }
            }
        else
            when(sortState.sortType) {
                ListsSortType.Name -> artists.toList().sortedByDescending { it.name }
                ListsSortType.Count -> artists.toList().sortedByDescending { it.items.size }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _playlistsSorted = combine(_playlists, _playlistsSortState) { playlists, sortState ->
        if (sortState.ascending)
            when(sortState.sortType) {
                ListsSortType.Name -> playlists.sortedBy { it.name }
                ListsSortType.Count -> playlists.sortedBy { it.items.size }
            }
        else
            when(sortState.sortType) {
                ListsSortType.Name -> playlists.sortedByDescending { it.name }
                ListsSortType.Count -> playlists.sortedByDescending { it.items.size }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val playlist = combine(_playlistsSorted, _playlistIndex) { playlists, index ->
        playlists.getOrNull(index) ?: Playlist()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Playlist())

    val bottomSheetPlaylist = combine(_playlistsSorted, _playlistIndex) { playlists, index ->
        playlists.getOrNull(index) ?: Playlist()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Playlist())

    val playlistsSorted = _playlistsSorted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val playlistFiles = combine(_playlistsSorted, _files, _playlistSortState, _playlistIndex) { playlists, files, sortState, index ->
        val list =
            playlists.getOrNull(index)?.items?.mapNotNull { item -> files.firstOrNull { it.path == item } } ?: emptyList()
        if (sortState.ascending)
            when(sortState.sortType) {
                PlaylistSortType.Custom -> list
                PlaylistSortType.Title -> list.sortedBy { it.title }
                PlaylistSortType.Duration -> list.sortedBy { it.duration }
                PlaylistSortType.Filename -> list.sortedBy { it.path }
            }
        else
            when(sortState.sortType) {
                PlaylistSortType.Custom -> list.reversed()
                PlaylistSortType.Title -> list.sortedByDescending { it.title }
                PlaylistSortType.Duration -> list.sortedByDescending { it.duration }
                PlaylistSortType.Filename -> list.sortedByDescending { it.path }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val bottomSheetPlaylistFiles = combine(_playlistsSorted, _files, _playlistIndex) { playlists, files, index ->
        playlists.getOrNull(index)?.items?.mapNotNull { item -> files.firstOrNull { it.path == item } } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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
        when(event) {
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
                    it.copy(expanded = false)
                }
            }
            SearchEvent.Expand -> {
                _searchState.update {
                    it.copy(expanded = true)
                }
            }
            is SearchEvent.UpdateExpanded -> {
                _searchState.update {
                    it.copy(expanded = event.expanded)
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
                    with (AudioFileIO.read(File(event.metadata.path))) {
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
                            arrayOf(this)
                        ) { _, _ -> }
                    }
                    viewModelScope.launch {
                        loadFiles()
                    }
                }
                else {
                    val editPendingIntent = MediaStore.createWriteRequest(context.contentResolver, listOf(event.metadata.uri))
                    context.startIntentSender(editPendingIntent.intentSender, null, 0, 0, 0)
                }
            }
        }
    }

    private fun onItemsSortEvent(event: SortEvent, state: MutableStateFlow<SortState>) {
        when(event) {
            SortEvent.Collapse -> state.update {
                it.copy(expanded = false)
            }
            SortEvent.Expand -> state.update {
                it.copy(expanded = true)
            }
            SortEvent.ToggleAscending -> state.update {
                it.copy(ascending = !it.ascending)
            }
            is SortEvent.UpdateAscending -> state.update {
                it.copy(ascending = event.ascending)
            }
            is SortEvent.UpdateExpanded -> state.update {
                it.copy(expanded = event.expanded)
            }
            is SortEvent.UpdateSortType -> state.update {
                it.copy(sortType = event.sortType)
            }
            is SortEvent.UpdateSortTypeOrToggleAsc -> {
                state.update {
                    if (it.sortType == event.sortType) it.copy(ascending = !it.ascending)
                    else it.copy(sortType = event.sortType)
                }
            }
        }
    }

    fun onSortEvent(event: SortEvent) = onItemsSortEvent(event, _sortState)

    fun onListSortEvent(event: SortEvent) = onItemsSortEvent(event, _listScreenSortState)

    fun onPlaylistSortEvent(event: PlaylistSortEvent) {
        when(event) {
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
                    if (it.sortType == event.sortType) it.copy(ascending = !it.ascending)
                    else it.copy(sortType = event.sortType)
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

    private fun onListsSortEvent(event: ListsSortEvent, state: MutableStateFlow<ListSortState>) {
        when(event) {
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
                    if (it.sortType == event.sortType) it.copy(ascending = !it.ascending)
                    else it.copy(sortType = event.sortType)
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
        _loading.value = true
        val list = mutableListOf<MusicCard>()
        withContext(Dispatchers.IO) {
            val cursor = context.contentResolver.query(
                collection, projection, selection, null, sortOrder
            )
            cursor?.use { crs ->
                val idColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val durationColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val titleColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumIdColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val albumColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val pathColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                while (crs.moveToNext()) {
                    val id = crs.getLong(idColumn)
                    val duration = crs.getLong(durationColumn)
                    val title = crs.getString(titleColumn)
                    val artist = crs.getString(artistColumn)
                    val albumId = crs.getLong(albumIdColumn)
                    val album = crs.getString(albumColumn)
                    val path = crs.getString(pathColumn)
                    val date = crs.getLong(dateColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
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
                            .setDate(
                                LocalDateTime
                                    .ofInstant(
                                        Instant.ofEpochMilli(date),
                                        TimeZone.getDefault().toZoneId()
                                    )
                            )
                            .setDuration(duration)
                            .setFavorite(isFavorite(path))
                            .setTimestamps(getTimestamps(path))
                            .build()
                    )
                }
                crs.close()
                initialized = true
            }
        }
        _albums.value = list.groupBy { it.album }.map { album ->
            Album(
                title = album.key,
                items = album.value.map { it.id },
                cover = null
            )
        }
        // do the same thing for _artists
        _artists.value = list.groupBy { it.artist }.map { artist ->
            Artist(
                name = artist.key,
                items = artist.value.map { it.id },
                cover = null
            )
        }
        _files.value = list
        _loading.value = false
        getThumbnails(list) {
            _files.value = list
        }
    }

    private fun getThumbnails(list: MutableList<MusicCard>, callback: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                list.forEachIndexed { index, file ->
                    val result = with(MediaMetadataRetriever()) {
                        try {
                            setDataSource(context, file.contentUri)
                            if (embeddedPicture != null)
                                BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture!!.size)!! to embeddedPicture!!
                            else null
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
                                    if (firstOrNull { it.title == file.album }?.cover == null)
                                        firstOrNull { it.title == file.album }?.cover = r.first
                                }.toList()
                            }
                            _artists.update { list ->
                                list.toMutableList().apply {
                                    if (firstOrNull { it.name == file.artist }?.cover == null)
                                        firstOrNull { it.name == file.artist }?.cover = r.first
                                }.toList()
                            }
                        }
                    }
                }
            }
            callback()
        }
    }

    private var controllerFuture: ListenableFuture<MediaController>
    private lateinit var player: Player

    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, MediaPlayerService::class.java))
        context.startForegroundService(Intent(context, MediaSessionService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            player = controllerFuture.get()
            player.addListener(object: Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    _playerState.update {
                        it.copy(loading = playbackState == Player.STATE_BUFFERING)
                    }
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    super.onRepeatModeChanged(repeatMode)
                    viewModelScope.launch {
                        _playerState.update {
                            it.copy(repeatMode = repeatMode)
                        }
                    }
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                    _playerState.update {
                        it.copy(shuffle = shuffleModeEnabled)
                    }
                }

                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                    super.onPlaybackParametersChanged(playbackParameters)
                    _playerState.update {
                        it.copy(speed = playbackParameters.speed)
                    }
                }
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying)
                        _playerState.update { it.copy(playState = PlayState.PLAYING) }
                    else
                        _playerState.update { it.copy(playState = PlayState.PAUSED) }
                }
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    _playerState.update { it.copy(index = player.currentMediaItemIndex) }
                    viewModelScope.launch {
                        try {
                            val files = queue.value
                            dao.upsertTimestamp(
                                Timestamp(
                                    files[player.currentMediaItemIndex].path,
                                    files[player.currentMediaItemIndex].timestamps.first() + LocalDateTime.now()
                                )
                            )
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                    if (
                        (_playerState.value.timer is TimerType.End) and
                        (
                                (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) or
                                        (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
                                )
                    )
                        if ((_playerState.value.timer as TimerType.End).tracks > 1)
                            _playerState.update {
                                val timer = (it.timer as TimerType.End)
                                it.copy(timer = timer.copy(tracks = timer.tracks - 1))
                            }
                        else onPlayerEvent(PlayerEvent.Stop)
                }
            })
            when (startupEvent) {
                StartupEvent.PlayFavorites ->
                    viewModelScope.launch {
                        val favorites = dao.suspendGetFavorites()
                        onPlayerEvent(PlayerEvent.Play(_files.value.filter { favorites.contains(it.path) }))
                    }
                StartupEvent.PlayMostPlayed ->
                    viewModelScope.launch {
                        val timestamps = dao.suspendGetTimestamps()
                        val mostPlayed = timestamps.toList().sortedByDescending { it.times.size }.map { it.path }
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

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), PlayerState())

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState())

    private var timerJob: Job? = null

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = null
        val timer = _playerState.value.timer
        if (timer == TimerType.Disabled) return
        timerJob = viewModelScope.launch {
            when(timer) {
                is TimerType.Duration -> {
                    while ((_playerState.value.timer as TimerType.Duration).ms > 0) {
                        delay(1000L)
                        _playerState.update {
                            it.copy(timer = TimerType.Duration((it.timer as TimerType.Duration).ms - 1000L))
                        }
                    }
                    onPlayerEvent(PlayerEvent.Stop)
                }
                is TimerType.Time -> {
                    while (true) {
                        val time = with (LocalDateTime.now()) { hour * 60 + minute }
                        if (abs(time - (timer.hour * 60 + timer.min)) == 0) break
                        delay(1000L)
                    }
                    onPlayerEvent(PlayerEvent.Stop)
                }
                else -> return@launch
            }
        }
    }

    fun onPlayerEvent(event: PlayerEvent) {
        when(event) {
            is PlayerEvent.Backward -> player.seekTo(player.currentPosition - event.ms)
            is PlayerEvent.Forward -> player.seekTo(player.currentPosition + event.ms)
            PlayerEvent.Next -> player.seekToNext()
            PlayerEvent.Pause -> player.pause()
            PlayerEvent.PauseResume -> {
                if (player.isPlaying) player.pause()
                else player.play()
            }
            is PlayerEvent.Play -> play(event.items, event.index)
            is PlayerEvent.PlayPaths ->
                play(
                    event.items.mapNotNull { item -> _files.value.firstOrNull { it.path == item } },
                    event.index
                )
            is PlayerEvent.PlayIds ->
                play(
                    event.items.mapNotNull { item -> _files.value.firstOrNull { it.id == item } },
                    event.index
                )
            PlayerEvent.Previous -> player.seekToPrevious()
            PlayerEvent.Resume -> player.play()
            is PlayerEvent.Seek -> player.seekTo(event.index, event.time)
            is PlayerEvent.SeekTime -> player.seekTo(event.time)
            PlayerEvent.Stop -> {
                timerJob?.cancel()
                timerJob = null
                player.stop()
                player.clearMediaItems()
                viewModelScope.launch {
                    _playerState.update {
                        it.copy(
                            playState = PlayState.STOP,
                            timer = TimerType.Disabled
                        )
                    }
                }
            }

            is PlayerEvent.Remove -> {
                player.removeMediaItem(event.index)
                viewModelScope.launch(Dispatchers.IO) {
                    _nowPlaying.update {
                        it.toMutableList().apply {
                            removeAt(event.index)
                        }
                    }
                }
            }
            is PlayerEvent.Swap -> {
                player.moveMediaItem(event.from, event.to)
                viewModelScope.launch(Dispatchers.IO) {
                    _nowPlaying.update {
                        it.toMutableList().apply {
                            add(event.to, removeAt(event.from))
                        }
                    }
                }
                _playerState.update {
                    it.copy(index = player.currentMediaItemIndex)
                }
            }
            PlayerEvent.CycleRepeatMode -> {
                player.repeatMode = when (player.repeatMode) {
                    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                    else -> Player.REPEAT_MODE_OFF
                }
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
                _playerState.update {
                    it.copy(timer = event.timer)
                }
                startTimer()
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
                    .map { _nowPlaying.value.indexOf(it.id) }
                    .filter { it >= 0 }
                    .forEach { player.removeMediaItem(it) }
                player.addMediaItems(
                    player.currentMediaItemIndex + 1, event.items.toMediaItems()
                )
                val items = event.items.map { item -> item.id }
                _nowPlaying.update {
                    it.toMutableList().apply {
                        removeAll(items)
                        addAll(player.currentMediaItemIndex + 1, items)
                    }
                }
                _playerState.update { it.copy(index = player.currentMediaItemIndex) }
            }
            is PlayerEvent.AddToQueue -> {
                if (_playerState.value.playState == PlayState.STOP) {
                    play(event.items, autoPlay = false)
                    return
                }
                val list = event.items.filter { item ->
                    !_nowPlaying.value.contains(item.id)
                }
                _nowPlaying.update {
                    it.toMutableList().apply { addAll(list.map { item -> item.id }) }
                }
                player.addMediaItems(list.toMediaItems())
            }
        }
    }

    fun onUiEvent(event: UiEvent) {
        when(event) {
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
            UiEvent.HideBottomSheet -> _uiState.update {
                it.copy(bottomSheetVisible = false)
            }
            is UiEvent.ShowBottomSheet -> _uiState.update {
                it.copy(bottomSheetItem = event.item, bottomSheetVisible = true)
            }
            UiEvent.HideListBottomSheet -> _uiState.update {
                it.copy(listBottomSheetVisible = false)
            }
            is UiEvent.ShowListBottomSheet -> _uiState.update {
                it.copy(
                    listBottomSheetList = event.list.toMusicCards(),
                    listBottomSheetTitle = event.title,
                    listBottomSheetText = event.text,
                    listBottomSheetImage = event.image,
                    listBottomSheetIcon = event.icon,
                    listBottomSheetVisible = true
                )
            }
            UiEvent.HideNewPlaylistDialog -> _uiState.update {
                it.copy(newPlaylistDialog = false)
            }
            UiEvent.ShowNewPlaylistDialog -> _uiState.update {
                it.copy(newPlaylistDialog = true)
            }
            is UiEvent.UpdateNewPlaylistName -> _uiState.update {
                it.copy(newPlaylistName = event.newName)
            }
            is UiEvent.ShowAddToPlaylistDialog -> _uiState.update {
                it.copy(addToPlaylistDialog = true, addToPlaylistItems = event.items)
            }
            UiEvent.HideAddToPlaylistDialog -> _uiState.update {
                it.copy(addToPlaylistDialog = false)
            }
            is UiEvent.UpdateSelectedPlaylist -> _uiState.update {
                it.copy(addToPlaylistIndex = event.index)
            }
            UiEvent.HidePlaylistBottomSheet -> _uiState.update {
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
            UiEvent.HideMetadataDialog -> _uiState.update {
                it.copy(metadataDialog = false)
            }
            is UiEvent.ShowMetadataDialog -> _uiState.update {
                it.copy(
                    metadataDialog = true,
                    metadata = event.metadata
                )
            }
            UiEvent.ToggleLyrics -> _uiState.update {
                it.copy(lyricsVisible = !it.lyricsVisible)
            }
        }
    }

    fun onMetadataEvent(event: MetadataEvent) {
        // generate when statement of event parameter
        when(event) {
            is MetadataEvent.Album -> _uiState.update {
                it.copy(metadata = it.metadata.copy(
                    newAlbum = event.album
                ))
            }
            is MetadataEvent.Artist -> _uiState.update {
                it.copy(metadata = it.metadata.copy(
                    newArtist = event.artist
                ))
            }
            is MetadataEvent.Composer -> _uiState.update {
                it.copy(metadata = it.metadata.copy(
                    newComposer = event.composer
                ))
            }
            is MetadataEvent.Genre -> _uiState.update {
                it.copy(metadata = it.metadata.copy(
                    newGenre = event.genre
                ))
            }
            is MetadataEvent.Title -> _uiState.update {
                it.copy(metadata = it.metadata.copy(
                    newTitle = event.title
                ))
            }
            is MetadataEvent.Year -> _uiState.update {
                it.copy(metadata = it.metadata.copy(
                    newYear = event.year
                ))
            }
        }
    }

    fun onPlaylistEvent(event: PlaylistEvent) {
        when(event) {
            is PlaylistEvent.AddToPlaylist -> {
                viewModelScope.launch {
                    with(_playlists.first()[_uiState.value.addToPlaylistIndex]) {
                        dao.upsertPlaylist(
                            copy(items = items +
                                    _uiState.value.addToPlaylistItems.filter { item ->
                                        !items.any { it == item }
                                    }
                            )
                        )
                    }
                }
            }
            PlaylistEvent.CreateNew -> {
                viewModelScope.launch {
                    dao.upsertPlaylist(Playlist(name = uiState.value.newPlaylistName))
                }
            }
            is PlaylistEvent.Reorder -> viewModelScope.launch {
                with(event.playlist) {
                    dao.upsertPlaylist(
                        copy(items = items.toMutableList().apply {
                                add(event.to, removeAt(event.from))
                            }
                        )
                    )
                }
            }
            is PlaylistEvent.RemoveAt -> viewModelScope.launch {
                with(event.playlist) {
                    dao.upsertPlaylist(
                        copy(items = items.toMutableList().apply {
                            removeAt(event.index)
                        })
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
        }
    }

    private fun play(list: List<MusicCard>, index: Int = 0, time: Long = 0L, autoPlay: Boolean = true) {
        if (list.isEmpty()) return
        if ((_nowPlaying == list) and (_playerState.value.playState != PlayState.STOP)) {
            if (index != _playerState.value.index) seek(index, time)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _nowPlaying.value = list.map { it.id }
        }
        player.setMediaItems(list.toMediaItems())
        player.seekTo(index, time)
        player.prepare()
        if (autoPlay) player.play()
        viewModelScope.launch {
            dao.upsertTimestamp(
                Timestamp(list[index].path, list[index].timestamps.first() + LocalDateTime.now())
            )
            _playerState.update {
                it.copy(
                    index = index,
                    time = time,
                    playState = if (autoPlay) PlayState.PLAYING else PlayState.PAUSED
                )
            }
            onUiEvent(UiEvent.SetViewState(ViewState.SMALL))
            while (true) {
                _playerState.update {
                    it.copy(time = player.currentPosition)
                }
                delay(100L)
            }
        }
    }

    private fun seek(index: Int, time: Long) {
        _playerState.update {
            it.copy(index = index, time = time)
        }
        player.seekTo(index, time)
    }

    // create a string list extension function that returns corresponding files

}
