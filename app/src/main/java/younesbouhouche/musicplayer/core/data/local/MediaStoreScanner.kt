package younesbouhouche.musicplayer.core.data.local

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.core.net.toFile
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import timber.log.Timber
import younesbouhouche.musicplayer.core.data.database.dao.AlbumsDao
import younesbouhouche.musicplayer.core.data.database.dao.ArtistsDao
import younesbouhouche.musicplayer.core.data.database.dao.SongsDao
import younesbouhouche.musicplayer.core.data.database.entities.AlbumEntity
import younesbouhouche.musicplayer.core.data.database.entities.ArtistEntity
import younesbouhouche.musicplayer.core.data.database.entities.SongEntity
import younesbouhouche.musicplayer.core.data.ext.getCoverUri
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.domain.models.LoadingState
import younesbouhouche.musicplayer.features.main.util.toFileUri
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.collections.plusAssign

private const val TAG = "MediaStoreScanner"

class MediaStoreScanner(private val context: Context) {
    data class MediaLibrary(
        val songs: List<SongEntity>,
        val albums: List<AlbumEntity>,
        val artists: List<ArtistEntity>
    )

    suspend fun scanMediaLibrary(
        onUpdate: (progress: Int, max: Int) -> Unit
    ): MediaLibrary {
        val songs = mutableListOf<SongEntity>()
        val albums = mutableListOf<AlbumEntity>()
        val artists = mutableListOf<ArtistEntity>()
        withContext(Dispatchers.IO) {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
                arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM_ARTIST,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.COMPOSER,
                    MediaStore.Audio.Media.GENRE,
                    MediaStore.Audio.Media.SIZE,
                    MediaStore.Audio.Media.YEAR,
                    MediaStore.Audio.Media.TRACK,
                    MediaStore.Audio.Media.CD_TRACK_NUMBER,
                ),
                MediaStore.Audio.Media.IS_MUSIC + "!= 0",
                null,
                MediaStore.Audio.Media.IS_MUSIC + "!= 0",
            )
            cursor?.use { crs ->
                val totalSongs = crs.count
                onUpdate(0, totalSongs)

                val idColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val fileNameColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val titleColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumArtistColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
                val pathColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val composerColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)
                val genreColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
                val sizeColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val yearColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val trackColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val discColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.CD_TRACK_NUMBER)

                var processedCount = 0
                while (crs.moveToNext()) {
                    val id = crs.getLong(idColumn)
                    val duration = crs.getLong(durationColumn)
                    val fileName = crs.getString(fileNameColumn)
                    val title = crs.getString(titleColumn)
                    val artist = crs.getString(artistColumn)
                    val album = crs.getString(albumColumn)
                    val albumArtist = crs.getStringOrNull(albumArtistColumn) ?: artist
                    val path = crs.getString(pathColumn)
                    val composer = crs.getStringOrNull(composerColumn) ?: ""
                    val genre = crs.getStringOrNull(genreColumn) ?: ""
                    val date = Files.getLastModifiedTime(Paths.get(path)).toMillis()
                    val size = crs.getLongOrNull(sizeColumn) ?: 0L
                    val year = crs.getStringOrNull(yearColumn)?.toIntOrNull()
                    val trackNumber = crs.getStringOrNull(trackColumn)?.toIntOrNull()
                    val discNumber = crs.getStringOrNull(discColumn)?.toIntOrNull()
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val lyrics = try {
                        AudioFileIO.read(path.toFileUri().toUri().toFile())
                            .tag
                            .getFirst(FieldKey.LYRICS)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                    songs += SongEntity.Builder()
                        .contentUri(contentUri)
                        .id(id)
                        .fileName(fileName)
                        .title(title)
                        .artist(artist)
                        .album(album)
                        .albumArtist(albumArtist)
                        .path(path)
                        .date(date)
                        .duration(duration)
                        .composer(composer)
                        .genre(genre)
                        .lyrics(lyrics)
                        .size(size)
                        .year(year)
                        .trackNumber(trackNumber)
                        .discNumber(discNumber)
                        .build()

                    processedCount++
                    onUpdate(processedCount, totalSongs)
                }
            }
            cursor?.close()
        }
        artists += songs.groupBy {
            it.artist
        }.map { (artistName, songsInArtist) ->
            val firstSongWithCover = songsInArtist.firstOrNull {
                it.coverUri != null
            }
            ArtistEntity(
                name = artistName,
                coverUri = firstSongWithCover?.coverUri
            )
        }

        return MediaLibrary(
            songs = songs,
            albums = albums,
            artists = artists
        )
    }

    suspend fun fetchSongsCover(
        songs: List<SongEntity>,
        onUpdate: (progress: Int) -> Unit
    ): List<SongEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val coversDir = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "covers"
                )
                if (!coversDir.exists()) {
                    coversDir.mkdirs()
                }

                var processedCount = 0

                val results = songs.map { song ->
                    async {
                        try {
                            // Check if cover is already cached
                            val result = if (song.coverPath.isNotEmpty()) {
                                val cachedFile = File(song.coverPath)
                                if (cachedFile.exists()) {
                                    val coverUri = context.getCoverUri(song.coverPath)
                                    song.copy(coverUri = coverUri)
                                } else {
                                    // Fetch cover using MediaMetadataRetriever
                                    val retriever = MediaMetadataRetriever()
                                    retriever.setDataSource(context, song.contentUri)
                                    val embeddedPicture = retriever.embeddedPicture
                                    retriever.release()

                                    if (embeddedPicture != null) {
                                        // Cache the cover to file
                                        val coverFile = File(coversDir, "cover_${song.id}.jpg")
                                        coverFile.writeBytes(embeddedPicture)

                                        val coverUri = context.getCoverUri(coverFile.absolutePath)
                                        song.copy(
                                            coverPath = coverFile.absolutePath,
                                            coverUri = coverUri
                                        )
                                    } else {
                                        song
                                    }
                                }
                            } else {
                                // Fetch cover using MediaMetadataRetriever
                                val retriever = MediaMetadataRetriever()
                                retriever.setDataSource(context, song.contentUri)
                                val embeddedPicture = retriever.embeddedPicture
                                retriever.release()

                                if (embeddedPicture != null) {
                                    // Cache the cover to file
                                    val coverFile = File(coversDir, "cover_${song.id}.jpg")
                                    coverFile.writeBytes(embeddedPicture)

                                    val coverUri = context.getCoverUri(coverFile.absolutePath)
                                    song.copy(
                                        coverPath = coverFile.absolutePath,
                                        coverUri = coverUri
                                    )
                                } else {
                                    song
                                }
                            }

                            synchronized(this@MediaStoreScanner) {
                                processedCount++
                                onUpdate(processedCount)
                            }

                            result
                        } catch (e: Exception) {
                            Timber.tag(TAG).e("Error fetching cover for song ${song.id}: ${e.message}")
                            synchronized(this@MediaStoreScanner) {
                                processedCount++
                                onUpdate(processedCount)
                            }
                            song
                        }
                    }
                }.awaitAll()

                results
            } catch (e: Exception) {
                Timber.tag(TAG).e("Error during cover processing: ${e.message}")
                songs
            }
        }
    }

    fun fetchAlbums(songs: List<SongEntity>) = songs.groupBy {
        it.album
    }.map { (albumName, songsInAlbum) ->
        val firstSongWithCover = songsInAlbum.firstOrNull {
            it.coverUri != null
        }
        AlbumEntity(
            name = albumName,
            cover = firstSongWithCover?.coverUri
        )
    }
}