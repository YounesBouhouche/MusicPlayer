package younesbouhouche.musicplayer.main.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.glance.presentation.MyAppWidget
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.AddToQueue
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Backward
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.CycleRepeatMode
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.DecreaseVolume
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Forward
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.IncreaseVolume
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Next
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Pause
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.PauseResume
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Play
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.PlayNext
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Previous
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Remove
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.ResetSpeed
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Resume
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Seek
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.SeekTime
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.SetPitch
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.SetPlayerVolume
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.SetRepeatMode
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.SetSpeed
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.SetTimer
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.SetVolume
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Stop
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.Swap
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.ToggleShuffle
import younesbouhouche.musicplayer.main.data.events.PlayerEvent.UpdateFavorite
import younesbouhouche.musicplayer.main.data.models.ArtistModel
import younesbouhouche.musicplayer.main.data.models.Queue
import younesbouhouche.musicplayer.main.data.util.getTag
import younesbouhouche.musicplayer.main.data.util.getThumbnail
import younesbouhouche.musicplayer.main.domain.events.FilesEvent
import younesbouhouche.musicplayer.main.domain.events.FilesEvent.AddFile
import younesbouhouche.musicplayer.main.domain.events.FilesEvent.LoadFiles
import younesbouhouche.musicplayer.main.domain.events.FilesEvent.RemoveFile
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.repo.ArtistsRepo
import younesbouhouche.musicplayer.main.domain.repo.FilesRepo
import younesbouhouche.musicplayer.main.domain.repo.PlayerRepo
import younesbouhouche.musicplayer.main.domain.util.onSuccess
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.util.getMimeType
import younesbouhouche.musicplayer.main.presentation.util.saveUriImageToInternalStorage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalCoroutinesApi::class)
class FilesRepoImpl(
    private val context: Context,
    private val artistsRepo: ArtistsRepo,
    private val mediaMetadataRetriever: MediaMetadataRetriever,
    private val dao: AppDao,
    val player: PlayerRepo,
): FilesRepo {
    private val _files = MutableStateFlow(emptyList<MusicCard>())
    private val _albums =
        _files.mapLatest {
            it.groupBy { it.album }.map { album ->
                Album(
                    name = album.key,
                    items = album.value.map { it.id },
                    cover = album.value.firstOrNull { it.cover.isNotEmpty() }?.cover,
                )
            }
        }
    private val _artists = MutableStateFlow(emptyList<Artist>())

    private val _playlists = dao.getPlaylists()

    private fun getFavorite(path: String) =
        dao.getFavorite(path).mapLatest { it == true }

    private fun getTimestamps(path: String) =
        dao.getTimestamps(path).mapLatest { it?.times ?: emptyList() }

    override fun init(scope: CoroutineScope, callback: () -> Unit) = player.init(scope, callback)

    override fun finalize() = player.finalize()

    override fun getFiles(): Flow<List<MusicCard>> = _files

    override fun getAlbums(): Flow<List<Album>> = _albums

    override fun getArtists(): Flow<List<Artist>> = _artists

    override fun getPlaylists(): Flow<List<Playlist>> = _playlists

    override fun getState(): StateFlow<PlayerState> = player.playerState.asStateFlow()

    private val _queue = dao.getQueue().map { it ?: Queue() }

    private val _queueList = _queue.map { it.items }

    private val _queueIndex = _queue.map { it.index }

    private val _queueFiles =
        combine(_queueList, _files) { ids, files ->
            ids.mapNotNull { id -> files.firstOrNull { it.id == id } }
        }

    override fun getCurrentItem(): Flow<MusicCard?> = combine(_queueFiles, _queueIndex) { list, index ->
        list.getOrNull(index)
    }.onEach {
        MyAppWidget().updateAll(context)
    }

    override suspend fun loadFiles() {
        val files = mutableListOf<MusicCard>()
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
                        MediaStore.Audio.Media.COMPOSER,
                        MediaStore.Audio.Media.GENRE,
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
                val composerColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)
                val genreColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
                while (crs.moveToNext()) {
                    val id = crs.getLong(idColumn)
                    val duration = crs.getLong(durationColumn)
                    val title = crs.getString(titleColumn)
                    val artist = crs.getString(artistColumn)
                    val albumId = crs.getLong(albumIdColumn)
                    val album = crs.getString(albumColumn)
                    val path = crs.getString(pathColumn)
                    val composer = crs.getStringOrNull(composerColumn) ?: ""
                    val genre = crs.getStringOrNull(genreColumn) ?: ""
                    val date = Files.getLastModifiedTime(Paths.get(path)).toMillis()
                    val contentUri: Uri =
                        ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id,
                        )
                    files +=
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
                            .setFavorite(getFavorite(path))
                            .setTimestamps(getTimestamps(path))
                            .setDuration(duration)
                            .setComposer(composer)
                            .setGenre(genre)
                            .build()
                }
                crs.close()
            }
            return@withContext
        }
        _files.value = files
        _artists.value = files.groupBy { it.artist }.map { artist ->
            Artist(
                name = artist.key,
                items = artist.value.map { it.id },
                picture = ""
            )
        }
        updateArtists()
        updateFilesMetadata()
    }

    suspend fun updateArtists() {
        withContext(Dispatchers.Main) {
            _artists.value.forEach { artist ->
                withContext(Dispatchers.IO) {
                    launch {
                        println("Getting artist : ${artist.name}")
                        var picture = ""
                        val model = dao.getArtist(artist.name)
                        if (model != null) picture = model.picture
                        else if (artist.name != "<unknown>")
                            artistsRepo.getArtist(artist.name).onSuccess {
                                it.data.firstOrNull()?.let { data ->
                                    picture = data.pictureBig
                                    dao.upsertArtist(ArtistModel(artist.name, data.pictureBig))
                                }
                            }
                        _artists.value = _artists.value.map {
                            if (it.name == artist.name)
                                it.copy(
                                    picture = picture
                                )
                            else it
                        }
                    }
                }
            }
        }
    }

    override suspend fun updateFilesMetadata() {
        withContext(Dispatchers.Main) {
            val time = measureTimeMillis {
                _files.value.forEach { file ->
                    withContext(Dispatchers.IO) {
                        launch {
                            val lyrics = File(file.path).getTag(FieldKey.LYRICS)
                            val cover = mediaMetadataRetriever.getThumbnail(context, file.contentUri)
                            _files.value = _files.value.map {
                                if (it.id == file.id) it.copy(lyrics = lyrics, cover = cover)
                                else it
                            }
                            if (cover.isNotEmpty())
                                _artists.value = _artists.value.map {
                                    if ((it.name == file.artist) and (it.cover?.isEmpty() != false))
                                        it.copy(cover = cover)
                                    else it
                                }
                        }
                    }
                }
            }
            println("Time to update metadata: $time ms")
        }
        MyAppWidget().updateAll(context)
    }

    override suspend fun isFavorite(path: String): Boolean =
        withContext(Dispatchers.IO) {
            getFavorite(path).first()
        }

    override suspend fun onFilesEvent(event: FilesEvent) {
        when (event) {
            LoadFiles -> loadFiles()
            is AddFile -> _files.value += event.file
            is RemoveFile -> _files.value -= event.file

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
                    loadFiles()
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
        MyAppWidget().updateAll(context)
    }

    override suspend fun onPlayerEvent(event: PlayerEvent) {
        player.onPlayerEvent(
            when(event) {
                is PlayerEvent.AddToQueue -> AddToQueue(event.items)
                is PlayerEvent.Backward -> Backward(event.ms)
                PlayerEvent.CycleRepeatMode -> CycleRepeatMode
                PlayerEvent.DecreaseVolume -> DecreaseVolume
                is PlayerEvent.Forward -> Forward(event.ms)
                PlayerEvent.IncreaseVolume -> IncreaseVolume
                PlayerEvent.Next -> Next
                PlayerEvent.Pause -> Pause
                PlayerEvent.PauseResume -> PauseResume
                is PlayerEvent.Play -> Play(event.items, event.index, event.shuffle)
                is PlayerEvent.PlayIds ->
                    Play(
                        event.items.mapNotNull {
                            _files.value.firstOrNull { file -> file.id == it }
                        },
                        event.index
                    )
                is PlayerEvent.PlayNext -> PlayNext(event.items)
                is PlayerEvent.PlayPaths ->
                    Play(
                        event.items.mapNotNull {
                            _files.value.firstOrNull { file -> file.path == it }
                        },
                        event.index
                    )
                PlayerEvent.Previous -> Previous
                is PlayerEvent.Remove -> Remove(event.index)
                PlayerEvent.ResetSpeed -> ResetSpeed
                PlayerEvent.Resume -> Resume
                is PlayerEvent.Seek -> Seek(event.index, event.time)
                is PlayerEvent.SeekTime -> SeekTime(event.time)
                is PlayerEvent.SetPitch -> SetPitch(event.pitch)
                is PlayerEvent.SetPlayerVolume -> SetPlayerVolume(event.volume)
                is PlayerEvent.SetRepeatMode -> SetRepeatMode(event.repeatMode)
                is PlayerEvent.SetSpeed -> SetSpeed(event.speed)
                is PlayerEvent.SetTimer -> SetTimer(event.timer)
                is PlayerEvent.SetVolume -> SetVolume(event.volume)
                PlayerEvent.Stop -> Stop
                is PlayerEvent.Swap -> Swap(event.from, event.to)
                PlayerEvent.ToggleShuffle -> ToggleShuffle
                is PlayerEvent.UpdateFavorite -> UpdateFavorite(event.path, event.favorite)
                PlayerEvent.PlayFavorites -> Play(_files.value.filter { it.favorite.first() })
                PlayerEvent.PlayMostPlayed -> Play(_files.value.sortedByDescending { it.date })
                is PlayerEvent.PlayPlaylist -> Play(
                    _playlists.first().first { it.id == event.id }.items.mapNotNull {
                        _files.value.firstOrNull { file -> file.path == it }
                    }
                )
            }
        )
        MyAppWidget().updateAll(context)
    }

    override suspend fun onPlaylistEvent(event: PlaylistEvent) {
        when (event) {
            is PlaylistEvent.AddToPlaylist ->
                with(_playlists.first()[event.index]) {
                    dao.upsertPlaylist(
                        copy(
                            items = items + event.items.filter { item -> !items.any { it == item } },
                        ),
                    )
                }

            is PlaylistEvent.CreateNewPlaylist ->
                dao.upsertPlaylist(
                    Playlist(
                        name = event.name,
                        items = event.items,
                    ),
                )

            is PlaylistEvent.CreateNew -> {
                val fileName = "pl_${_playlists.first().size}.jpg"
                val saved =
                    event.image?.let {
                        withContext(Dispatchers.IO) {
                            saveUriImageToInternalStorage(context, it, fileName) != null
                        }
                    } == true
                dao.upsertPlaylist(
                    Playlist(
                        name = event.name,
                        items = event.items,
                        image = if (saved) fileName else null,
                    ),
                )
            }

            is PlaylistEvent.Reorder ->
                with(event.playlist.toPlaylist()) {
                    dao.upsertPlaylist(
                        copy(
                            items =
                                items.toMutableList().apply {
                                    add(event.to, removeAt(event.from))
                                }
                        ),
                    )
                }

            is PlaylistEvent.RemoveAt ->
                with(event.playlist.toPlaylist()) {
                    dao.upsertPlaylist(
                        copy(
                            items =
                                items.toMutableList().apply {
                                    removeAt(event.index)
                                },
                        ),
                    )
                }

            is PlaylistEvent.DeletePlaylist -> dao.deletePlaylist(event.playlist)

            is PlaylistEvent.DeleteUiPlaylist ->
                onPlaylistEvent(PlaylistEvent.DeletePlaylist(event.playlist.toPlaylist()))

            is PlaylistEvent.RenamePlaylist -> dao.updatePlaylistName(event.id, event.name)

            is PlaylistEvent.SetFavorite -> dao.setPlaylistFavorite(event.id, event.favorite)
        }
    }
}