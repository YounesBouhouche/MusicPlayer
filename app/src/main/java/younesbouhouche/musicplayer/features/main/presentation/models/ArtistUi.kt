package younesbouhouche.musicplayer.features.main.presentation.models

import android.net.Uri
import younesbouhouche.musicplayer.core.domain.models.MusicCard

data class ArtistUi(
    val name: String = "",
    val items: List<MusicCard> = emptyList(),
    var cover: Uri? = null,
    var picture: String = "",
) {
    fun getPicture(): Any? = picture.takeIf { it.isNotEmpty() }
        ?: cover
        ?: items.firstOrNull { it.coverUri != null }?.coverUri
}
