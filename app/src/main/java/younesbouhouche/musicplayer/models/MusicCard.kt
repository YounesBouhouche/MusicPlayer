package younesbouhouche.musicplayer.models

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import younesbouhouche.musicplayer.states.MusicMetadata
import java.time.LocalDateTime

@Immutable
data class MusicCard(
    var contentUri: Uri,
    var id: Long,
    var title: String,
    var cover: Bitmap?,
    var coverByteArray: ByteArray,
    var artist: String,
    var albumId: Long,
    var album: String,
    var path: String,
    var date: LocalDateTime,
    var duration: Long,
    var favorite: StateFlow<Boolean>,
    var timestamps: StateFlow<List<LocalDateTime>>,
)  {
    class Builder(card: MusicCard? = null) {
        private var contentUri: Uri = Uri.EMPTY
        private var id: Long = 0
        private var title: String = ""
        private var cover: Bitmap? = null
        private var coverByteArray: ByteArray = byteArrayOf()
        private var artist: String = ""
        private var albumId: Long = 0
        private var album: String = ""
        private var path: String = ""
        private var date: LocalDateTime = LocalDateTime.now()
        private var duration: Long = 0
        private var favorite: StateFlow<Boolean> = MutableStateFlow(false)
        private var timestamps: StateFlow<List<LocalDateTime>> = MutableStateFlow(emptyList())

        fun setContentUri(contentUri: Uri) = apply { this.contentUri = contentUri }
        fun setId(id: Long) = apply { this.id = id }
        fun setTitle(title: String) = apply { this.title = title }
        fun setCover(cover: Bitmap?) = apply { this.cover = cover }
        fun setCoverByteArray(coverByteArray: ByteArray) = apply { this.coverByteArray = coverByteArray }
        fun setArtist(artist: String) = apply { this.artist = artist }
        fun setAlbumId(albumId: Long) = apply { this.albumId = albumId }
        fun setAlbum(album: String) = apply { this.album = album }
        fun setPath(path: String) = apply { this.path = path }
        fun setDate(date: LocalDateTime) = apply { this.date = date }
        fun setDuration(duration: Long) = apply { this.duration = duration }
        fun setFavorite(favorite: StateFlow<Boolean>) = apply { this.favorite = favorite }
        fun setTimestamps(timestamps: StateFlow<List<LocalDateTime>>) = apply { this.timestamps = timestamps }

        fun build() = MusicCard(
            contentUri = contentUri,
            id = id,
            title = title,
            cover = cover,
            coverByteArray = coverByteArray,
            artist = artist,
            albumId = albumId,
            album = album,
            path = path,
            date = date,
            duration = duration,
            favorite = favorite,
            timestamps = timestamps
        )

        init {
            card?.let {
                contentUri = it.contentUri
                id = it.id
                title = it.title
                cover = it.cover
                coverByteArray = it.coverByteArray
                artist = it.artist
                albumId = it.albumId
                album = it.album
                path = it.path
                date = it.date
                duration = it.duration
                favorite = it.favorite
                timestamps = it.timestamps
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MusicCard

        if (contentUri != other.contentUri) return false
        if (id != other.id) return false
        if (cover != other.cover) return false
        if (!coverByteArray.contentEquals(other.coverByteArray)) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (albumId != other.albumId) return false
        if (album != other.album) return false
        if (path != other.path) return false
        if (date != other.date) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentUri.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (cover?.hashCode() ?: 0)
        result = 31 * result + coverByteArray.contentHashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + albumId.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
    fun toMetadata() = MusicMetadata(
        uri = contentUri,
        path = path,
        newTitle = title,
        newAlbum = album,
        newArtist = artist,
    )
    fun toMediaItem() = MediaItem
        .Builder()
        .setUri(contentUri)
        .setMediaId("$id")
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setAlbumArtist(artist)
                .setArtworkData(coverByteArray, MediaMetadata.PICTURE_TYPE_MEDIA)
                .build()
        )
        .build()
}