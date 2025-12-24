package younesbouhouche.musicplayer.core.data.database.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class SongEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val contentUri: Uri,
    val fileName: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String,
    val duration: Long,
    val path: String,
    val date: Long = System.currentTimeMillis(),
    val size: Long = 0,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val year: Int? = null,
    val composer: String?,
    val genre: String?,
    val lyrics: String?,
    var cover: ByteArray?,
    var coverUri: Uri?,
    var coverPath: String
) {
    class Builder {
        private var id: Long = 0
        private var contentUri: Uri = Uri.EMPTY
        private var fileName: String = ""
        private var title: String = ""
        private var artist: String = ""
        private var album: String = ""
        private var albumArtist: String = ""
        private var duration: Long = 0
        private var path: String = ""
        private var date: Long = System.currentTimeMillis()
        private var size: Long = 0
        private var trackNumber: Int? = null
        private var discNumber: Int? = null
        private var year: Int? = null
        private var composer: String? = null
        private var genre: String? = null
        private var lyrics: String? = null
        private var cover: ByteArray? = null
        private var coverUri: Uri? = null
        private var coverPath: String = ""

        fun id(id: Long) = apply { this.id = id }
        fun contentUri(contentUri: Uri) = apply { this.contentUri = contentUri }
        fun fileName(fileName: String) = apply { this.fileName = fileName }
        fun title(title: String) = apply { this.title = title }
        fun artist(artist: String) = apply { this.artist = artist }
        fun album(album: String) = apply { this.album = album }
        fun albumArtist(albumArtist: String) = apply { this.albumArtist = albumArtist }
        fun duration(duration: Long) = apply { this.duration = duration }
        fun path(path: String) = apply { this.path = path }
        fun date(date: Long) = apply { this.date = date }
        fun size(size: Long) = apply { this.size = size }
        fun trackNumber(trackNumber: Int?) = apply { this.trackNumber = trackNumber }
        fun discNumber(discNumber: Int?) = apply { this.discNumber = discNumber }
        fun year(year: Int?) = apply { this.year = year }
        fun composer(composer: String?) = apply { this.composer = composer }
        fun genre(genre: String?) = apply { this.genre = genre }
        fun lyrics(lyrics: String?) = apply { this.lyrics = lyrics }
        fun cover(cover: ByteArray?) { this.cover = cover }
        fun coverUri(coverUri: Uri?) { this.coverUri = coverUri }
        fun coverPath(coverPath: String) { this.coverPath = coverPath }

        fun build() = SongEntity(
            id = id,
            contentUri = contentUri,
            fileName = fileName,
            title = title,
            artist = artist,
            album = album,
            albumArtist = albumArtist,
            duration = duration,
            path = path,
            date = date,
            size = size,
            trackNumber = trackNumber,
            discNumber = discNumber,
            year = year,
            composer = composer,
            genre = genre,
            lyrics = lyrics,
            cover = cover,
            coverUri = coverUri,
            coverPath = coverPath,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SongEntity

        if (id != other.id) return false
        if (duration != other.duration) return false
        if (date != other.date) return false
        if (size != other.size) return false
        if (trackNumber != other.trackNumber) return false
        if (discNumber != other.discNumber) return false
        if (year != other.year) return false
        if (contentUri != other.contentUri) return false
        if (fileName != other.fileName) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (albumArtist != other.albumArtist) return false
        if (path != other.path) return false
        if (composer != other.composer) return false
        if (genre != other.genre) return false
        if (lyrics != other.lyrics) return false
        if (!cover.contentEquals(other.cover)) return false
        if (coverUri != other.coverUri) return false
        if (coverPath != other.coverPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + (trackNumber ?: 0)
        result = 31 * result + (discNumber ?: 0)
        result = 31 * result + (year ?: 0)
        result = 31 * result + contentUri.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + (composer?.hashCode() ?: 0)
        result = 31 * result + (genre?.hashCode() ?: 0)
        result = 31 * result + (lyrics?.hashCode() ?: 0)
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        result = 31 * result + (coverUri?.hashCode() ?: 0)
        result = 31 * result + coverPath.hashCode()
        return result
    }
}