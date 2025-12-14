package younesbouhouche.musicplayer.core.domain.models

import android.net.Uri

data class Album(
    val name: String,
    val picture: String? = null,
    val cover: Uri? = null,
    val songs: List<Song> = emptyList()
) {
    fun search(query: String) = name.lowercase().contains(query.lowercase())
}