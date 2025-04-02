package younesbouhouche.musicplayer.core.domain.models

import younesbouhouche.musicplayer.main.presentation.models.ArtistUi

data class Artist(
    val name: String = "",
    val items: List<Long> = emptyList(),
    var cover: ByteArray? = null,
    var picture: String = ""
) {
    fun toArtistUi(files: (List<Long>) -> List<MusicCard>) = ArtistUi(name, files(items), cover, picture)
    fun getPicture(): Any? = picture.takeIf { it.isNotEmpty() } ?: cover
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artist

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