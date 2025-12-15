package younesbouhouche.musicplayer.core.domain.models

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import younesbouhouche.musicplayer.core.data.database.entities.PlayHistEntity

data class Song(
    val id: Long = 0,
    val contentUri: Uri = Uri.EMPTY,
    val fileName: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val duration: Long = 0,
    val path: String = "",
    val date: Long = System.currentTimeMillis(),
    val size: Long = 0,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val year: Int? = null,
    val composer: String? = null,
    val genre: String? = null,
    var coverPath: String? = null,
    var coverUri: Uri? = null,
    val isFavorite: Boolean = false,
    val playHistory: List<PlayHistEntity> = emptyList()
) {
    class Builder {
        private var id: Long = 0
        private lateinit var contentUri: Uri
        private var fileName: String = ""
        private var title: String = ""
        private var artist: String = ""
        private var album: String = ""
        private var duration: Long = 0
        private var path: String = ""
        private var date: Long = System.currentTimeMillis()
        private var size: Long = 0
        private var trackNumber: Int? = null
        private var discNumber: Int? = null
        private var year: Int? = null
        private var composer: String? = null
        private var genre: String? = null
        private var coverPath: String? = null
        private var coverUri: Uri? = null
        private var isFavorite: Boolean = false
        private var playHistory: List<PlayHistEntity> = emptyList()

        fun id(id: Long) = apply { this.id = id }
        fun contentUri(contentUri: Uri) = apply { this.contentUri = contentUri }
        fun fileName(fileName: String) = apply { this.fileName = fileName }
        fun title(title: String) = apply { this.title = title }
        fun artist(artist: String) = apply { this.artist = artist }
        fun album(album: String) = apply { this.album = album }
        fun duration(duration: Long) = apply { this.duration = duration }
        fun path(path: String) = apply { this.path = path }
        fun date(date: Long) = apply { this.date = date }
        fun size(size: Long) = apply { this.size = size }
        fun trackNumber(trackNumber: Int?) = apply { this.trackNumber = trackNumber }
        fun discNumber(discNumber: Int?) = apply { this.discNumber = discNumber }
        fun year(year: Int?) = apply { this.year = year }
        fun composer(composer: String?) = apply { this.composer = composer }
        fun genre(genre: String?) = apply { this.genre = genre }
        fun coverPath(coverPath: String?) = apply { this.coverPath = coverPath }
        fun coverUri(coverUri: Uri?) = apply { this.coverUri = coverUri }
        fun isFavorite(isFavorite: Boolean) = apply { this.isFavorite = isFavorite }
        fun playHistory(playHistory: List<PlayHistEntity>) =
            apply { this.playHistory = playHistory }

        fun build() = Song(
            id,
            contentUri,
            fileName,
            title,
            artist,
            album,
            duration,
            path,
            date,
            size,
            trackNumber,
            discNumber,
            year,
            composer,
            genre,
            coverPath,
            coverUri,
            isFavorite,
            playHistory
        )
    }

    fun toMediaItem(): MediaItem {
        return MediaItem
                .Builder()
                .setUri(contentUri)
                .setMediaId("$id")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(title)
                        .setArtist(artist)
                        .setAlbumTitle(album)
                        .setArtist(artist)
                        .setGenre(genre)
                        .setComposer(composer)
                        .setArtworkData(
                            MediaItem.fromUri(contentUri).mediaMetadata.artworkData,
                            MediaMetadata.PICTURE_TYPE_MEDIA
                        )
                        .build()
                )
                .build()
    }
}