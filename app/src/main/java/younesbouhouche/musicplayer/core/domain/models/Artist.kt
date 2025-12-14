package younesbouhouche.musicplayer.core.domain.models

import android.net.Uri

data class Artist(
    val name: String,
    val picture: String? = null,
    val coverUri: Uri? = null,
    val songs: List<Song> = emptyList()
) {
    fun getPicture() = picture ?: coverUri
    fun search(query: String) = name.lowercase().contains(query.lowercase())
}