package younesbouhouche.musicplayer.features.main.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.ItemData
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.domain.models.getCoverUri
import younesbouhouche.musicplayer.features.main.data.dao.AppDao
import younesbouhouche.musicplayer.features.main.domain.models.LoadingState
import younesbouhouche.musicplayer.features.main.domain.repo.ArtistsRepository
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.features.main.domain.util.onSuccess
import younesbouhouche.musicplayer.features.main.presentation.util.search
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

private const val TAG = "MediaRepositoryImpl"
private const val BATCH_SIZE = 15 // Process covers in batches of 15

@OptIn(ExperimentalCoroutinesApi::class)
class MediaRepositoryImpl(
    private val context: Context,
    private val artistsRepository: ArtistsRepository,
    private val dao: AppDao
) : MediaRepository {
    // Progress reporting for UI feedback
    private val _loadingProgress = MutableStateFlow(LoadingState(stepsCount = 3))

    // State flow for all music files
    private val _files = MutableStateFlow(emptyList<MusicCard>())
    private val _favorites = dao.getFavorites()

    // Map of file paths to their cover URIs for caching
    private val coverCache = mutableMapOf<String, Uri?>()

    private val _albums = _files.mapLatest { files ->
        files.groupBy { it.album }.map { album ->
            Album(
                name = album.key,
                items = album.value.map { it.id },
                cover = album.value.firstOrNull { it.coverUri != null }?.coverUri,
            )
        }
    }
    private val _artists = MutableStateFlow(emptyList<Artist>())

    override fun getAllMedia(): Flow<List<MusicCard>> = combine(_files, _favorites) { files, favorites ->
        files.map { file ->
            file.copy(favorite = favorites.contains(file.path))
        }
    }

    override fun getFavorites(): Flow<List<MusicCard>> = _favorites.map { favorites ->
        _files.value.filter { file -> favorites.contains(file.path) }
    }

    override fun getAlbums(): Flow<List<Album>> = _albums
    override fun getArtists(): Flow<List<Artist>> = _artists.asStateFlow()

    override fun searchMedia(query: String): Flow<List<MusicCard>> {
        return _files.map {
            it.filter { files ->
                files.search(query)
            }
        }
    }

    private fun getTimestamps(path: String) = dao.getTimestamps(path).mapLatest { it?.times ?: emptyList() }

    override suspend fun refreshMediaLibrary(callback: suspend () -> Unit) {
        val files = mutableListOf<MusicCard>()
        _loadingProgress.update {
            it.copy(stepsCount = 3)
        }
        withContext(Dispatchers.IO) {
            // Now fetch the actual file data
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
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.COMPOSER,
                    MediaStore.Audio.Media.GENRE,
                    MediaStore.Audio.Media.SIZE,
                ),
                MediaStore.Audio.Media.IS_MUSIC + "!= 0",
                null,
                MediaStore.Audio.Media.IS_MUSIC + "!= 0",
            )
            cursor?.use { crs ->
                // First, count the total number of audio files
                val totalFiles = crs.count
                // Update loading state to reflect we're starting file processing
                _loadingProgress.update {
                    it.copy(step = 0, progress = 0, progressMax = totalFiles)
                }

                val idColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val fileNameColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val titleColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumIdColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val albumColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val pathColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val composerColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)
                val genreColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
                val sizeColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                var processedFiles = 0
                while (crs.moveToNext()) {
                    // Yield frequently to keep the UI responsive
                    if (processedFiles % 20 == 0) {
                        yield()
                    }

                    val id = crs.getLong(idColumn)
                    val duration = crs.getLong(durationColumn)
                    val fileName = crs.getString(fileNameColumn)
                    val title = crs.getString(titleColumn)
                    val artist = crs.getString(artistColumn)
                    val albumId = crs.getLong(albumIdColumn)
                    val album = crs.getString(albumColumn)
                    val path = crs.getString(pathColumn)
                    val composer = crs.getStringOrNull(composerColumn) ?: ""
                    val genre = crs.getStringOrNull(genreColumn) ?: ""
                    val date = Files.getLastModifiedTime(Paths.get(path)).toMillis()
                    val size = crs.getLongOrNull(sizeColumn) ?: 0L
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    files += MusicCard.Builder()
                        .setContentUri(contentUri)
                        .setId(id)
                        .setFileName(fileName)
                        .setTitle(title)
                        .setArtist(artist)
                        .setAlbum(album)
                        .setAlbumId(albumId)
                        .setPath(path)
                        .setDate(date)
                        .setTimestamps(getTimestamps(path))
                        .setDuration(duration)
                        .setComposer(composer)
                        .setGenre(genre)
                        .setSize(size)
                        .build()

                    // Update progress after processing each file
                    processedFiles++
                    _loadingProgress.update {
                        it.copy(progress = processedFiles)
                    }
                }
                crs.close()
            }
        }

        // Update files list and prepare for next steps
        _files.value = files
        yield() // Ensure UI gets a chance to update

        val artists = files.groupBy { it.artist }.map { artist ->
            Artist(
                name = artist.key,
                items = artist.value.map { it.id },
                cover = artist.value.firstOrNull { it.coverUri != null }?.coverUri
            )
        }
        _artists.value = artists

        // Mark step 0 (file loading) as complete
        _loadingProgress.update {
            it.copy(progress = 1, progressMax = 1, step = 0)
        }

        // Call the callback on the main thread
        withContext(Dispatchers.Main) {
            callback()
        }

        // Optimized cover extraction process
        withContext(Dispatchers.IO) {
            try {
                // Create covers directory in app-specific external storage
                val coversDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "covers")
                if (!coversDir.exists()) {
                    coversDir.mkdirs()
                }

                // Group files by potential cover path to avoid duplicate work
                val filesByCoverPath = _files.value.groupBy { file ->
                    File(coversDir, "${file.path.replace("/", "_")}.jpg").absolutePath
                }

                val totalPaths = filesByCoverPath.size
                var completedPaths = 0
                // Reset progress for step 1 (cover extraction)
                _loadingProgress.update {
                    it.copy(progress = 0, progressMax = totalPaths, step = 1)
                }

                // Process in smaller batches to avoid overwhelming the system
                val batches = filesByCoverPath.entries.chunked(BATCH_SIZE / 3) // Reduce batch size

                for (batch in batches) {
                    // Process each batch concurrently but limit the concurrency
                    val updates = batch.map { (coverPath, filesWithSameCover) ->
                        async(Dispatchers.IO) {
                            // Allow cancellation between files
                            yield()

                            val coverFile = File(coverPath)
                            val updatedFiles = mutableListOf<Pair<String, Pair<String, Uri?>>>()

                            // Only process if cover doesn't already exist
                            if (!coverFile.exists()) {
                                val firstFile = filesWithSameCover.first()
                                val retriever = MediaMetadataRetriever()
                                try {
                                    retriever.setDataSource(firstFile.path)
                                    retriever.embeddedPicture?.let { cover ->
                                        try {
                                            // Write cover to file system
                                            coverFile.outputStream().use { output ->
                                                output.write(cover)
                                                output.flush()
                                            }
                                        } catch (e: Exception) {
                                            Timber.tag(TAG)
                                                .e("Failed to write cover art: ${e.message}")
                                        }
                                    }
                                } catch (e: Exception) {
                                    Timber.tag(TAG).e("Error extracting cover: ${e.message}")
                                } finally {
                                    retriever.release()
                                }
                            }

                            // Prepare updates for all files sharing this cover
                            val coverUri = if (coverFile.exists()) {
                                context.getCoverUri(coverPath)
                            } else null

                            // Store in cache for quick lookup
                            coverCache[coverPath] = coverUri

                            // Update all files that use this cover path
                            filesWithSameCover.forEach { file ->
                                updatedFiles.add(file.path to (coverPath to coverUri))
                            }

                            // Update progress after each cover is processed
                            completedPaths++
                            _loadingProgress.update {
                                it.copy(progress = completedPaths, progressMax = totalPaths)
                            }

                            updatedFiles
                        }
                    }.awaitAll().flatten()

                    // Apply batch updates to _files at once to reduce UI updates
                    if (updates.isNotEmpty()) {
                        _files.update { currentFiles ->
                            currentFiles.map { existingFile ->
                                updates.find { it.first == existingFile.path }?.let { (_, coverData) ->
                                    val (coverPath, coverUri) = coverData
                                    existingFile.copy(
                                        coverPath = coverPath,
                                        coverUri = coverUri
                                    )
                                } ?: existingFile
                            }
                        }

                        // Allow UI to catch up
                        yield()
                    }
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e("Error during metadata processing: ${e.message}")
                _loadingProgress.update {
                    it.copy(progress = 1, progressMax = 1) // Mark as complete even if there was an error
                }
            }
        }

        // Fetch artist data
        // Switch to a completely non-blocking approach
        withContext(Dispatchers.IO) {
            val artistsCount = _artists.value.size
            _loadingProgress.update {
                it.copy(progress = 0, progressMax = artistsCount, step = 2)
            }

            // Process artists in very small batches for better responsiveness
            val artistBatchSize = 3
            val artistBatches = _artists.value.withIndex().chunked(artistBatchSize)

            val updatedArtists = _artists.value.toMutableList()
            var processedCount = 0

            for (batch in artistBatches) {
                yield() // Allow UI thread to process before each batch

                val batchResults = batch.map { (index, artist) ->
                    async(Dispatchers.IO) {
                        yield() // Allow cancellation

                        var picture = ""
                        if (artist.name != "<unknown>") {
                            try {
                                // Use a timeout for artist repository calls to prevent hanging
                                kotlinx.coroutines.withTimeout(5000) {
                                    artistsRepository.getArtist(artist.name).onSuccess { result ->
                                        picture = result ?: ""
                                    }
                                }
                            } catch (e: Exception) {
                                Timber.tag(TAG).e("Error fetching artist data: ${e.message}")
                            }
                        }

                        processedCount++
                        // Report progress - do this outside the loop to avoid flooding UI updates
                        if (processedCount % 3 == 0 || processedCount == artistsCount) {
                            _loadingProgress.update {
                                it.copy(progress = processedCount)
                            }
                        }

                        Triple(index, artist, picture)
                    }
                }.awaitAll()

                // Buffer the updates to reduce UI state changes
                val artistUpdates = mutableListOf<Pair<Int, Artist>>()

                // Process batch results
                for ((index, artist, picture) in batchResults) {
                    val updatedArtist = artist.copy(picture = picture)
                    updatedArtists[index] = updatedArtist
                    artistUpdates.add(index to updatedArtist)
                }

                // Apply updates all at once and yield for UI thread
                if (artistUpdates.isNotEmpty()) {
                    _artists.value = updatedArtists.toList()
                    yield() // Critical yield to ensure UI gets updated
                }

                // Small delay between batches to ensure UI responsiveness
                kotlinx.coroutines.delay(50)
            }

            // Final update to ensure UI is in sync
            _artists.value = updatedArtists.toList()
        }

        // Ensure progress reaches 100%
        _loadingProgress.update {
            it.copy(progress = _artists.value.size, progressMax = _artists.value.size, step = 2)
        }
    }

    override fun getFavorite(path: String): Flow<Boolean> {
        return dao.getFavorite(path).map { it == true }
    }

    override suspend fun setFavorite(path: String, favorite: Boolean) =
        dao.upsertItem(ItemData(path = path, favorite = favorite))

    override fun getLoading(): StateFlow<LoadingState> =_loadingProgress

    override fun getMediaById(id: Long): Flow<MusicCard?> {
        return _files.map { files ->
            files.firstOrNull { it.id == id }
        }
    }
    override fun getMediaByPath(path: String): Flow<MusicCard?> {
        return _files.map { files ->
            files.firstOrNull { it.path == path }
        }
    }

    override suspend fun suspendGetMediaById(id: Long): MusicCard? = getMediaById(id).first()
    override suspend fun suspendGetMediaByPath(path: String): MusicCard? = getMediaByPath(path).first()

    override suspend fun getUriById(id: Long): Uri? {
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
            arrayOf(MediaStore.Audio.Media._ID),
            "${MediaStore.Audio.Media._ID}=?",
            arrayOf(id.toString()),
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val foundId = it.getLong(idx)
                return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, foundId)
            }
        }
        return null
    }
}

