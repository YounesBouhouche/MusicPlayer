package younesbouhouche.musicplayer.core.domain.models

import android.net.Uri

data class Album(
    val name: String = "",
    val items: List<Long> = emptyList(),
    var cover: Uri? = null,
) {
    fun search(query: String): Boolean {
        return name.contains(query, ignoreCase = true)
    }
}