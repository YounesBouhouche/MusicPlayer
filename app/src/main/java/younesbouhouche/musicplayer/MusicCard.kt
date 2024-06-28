package younesbouhouche.musicplayer

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.StateFlow
import younesbouhouche.musicplayer.states.MusicMetadata
import java.time.LocalDateTime

@Immutable
data class MusicCard(
    val contentUri: Uri,
    val id: Long,
    var title: String,
    var cover: Bitmap?,
    var coverByteArray: ByteArray,
    var artist: String,
    val albumId: Long,
    var album: String,
    val path: String,
    val date: LocalDateTime,
    val duration: Long,
    val favorite: StateFlow<Boolean>,
    val timestamps: StateFlow<List<LocalDateTime>>,
)  {
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
}

fun (Pair<String, String>).containEachOther() =
    first.contains(second) or second.contains(first)

fun MusicCard.search(query: String) =
    (title to query).containEachOther() or
            (path to query).containEachOther() or
            (album to query).containEachOther() or
            (artist to query).containEachOther()
