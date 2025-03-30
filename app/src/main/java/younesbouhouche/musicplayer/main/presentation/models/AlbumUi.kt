package younesbouhouche.musicplayer.main.presentation.models

import younesbouhouche.musicplayer.core.domain.models.MusicCard

data class AlbumUi(
    val name: String = "",
    val items: List<MusicCard> = emptyList(),
    var cover: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AlbumUi

        if (name != other.name) return false
        if (items != other.items) return false
        if (!cover.contentEquals(other.cover)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        return result
    }
}
