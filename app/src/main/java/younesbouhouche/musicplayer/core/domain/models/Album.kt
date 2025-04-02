package younesbouhouche.musicplayer.core.domain.models

import kotlin.text.isNotEmpty

data class Album(
    val name: String = "",
    val items: List<Long> = emptyList(),
    var cover: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

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