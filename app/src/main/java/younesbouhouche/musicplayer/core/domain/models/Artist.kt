package younesbouhouche.musicplayer.core.domain.models

import android.net.Uri
import younesbouhouche.musicplayer.features.main.presentation.models.ArtistUi

data class Artist(
    val name: String = "",
    val items: List<Long> = emptyList(),
    var cover: Uri? = null,
    var picture: String = ""
) {
    fun toArtistUi(files: (List<Long>) -> List<MusicCard>) = ArtistUi(name, files(items), cover, picture)
    fun getPicture(): Any? = picture.takeIf { it.isNotEmpty() } ?: cover

    fun search(query: String): Boolean {
        return name.contains(query, ignoreCase = true)
    }
}