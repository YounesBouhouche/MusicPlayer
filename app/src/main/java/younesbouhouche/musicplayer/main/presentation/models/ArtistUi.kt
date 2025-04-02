package younesbouhouche.musicplayer.main.presentation.models

import android.graphics.Bitmap
import younesbouhouche.musicplayer.core.domain.models.MusicCard

data class ArtistUi(
    val name: String = "",
    val items: List<MusicCard> = emptyList(),
    var cover: ByteArray? = null,
    var picture: String = "",
) {
    fun getPicture(): Any? = picture.takeIf { it.isNotEmpty() } ?: cover
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArtistUi

        if (name != other.name) return false
        if (items != other.items) return false
        if (!cover.contentEquals(other.cover)) return false
        if (picture != other.picture) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        result = 31 * result + picture.hashCode()
        return result
    }
}
