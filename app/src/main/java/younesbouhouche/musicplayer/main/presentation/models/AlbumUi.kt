package younesbouhouche.musicplayer.main.presentation.models

import android.net.Uri
import younesbouhouche.musicplayer.core.domain.models.MusicCard

data class AlbumUi(
    val name: String = "",
    val items: List<MusicCard> = emptyList(),
    var cover: Uri? = null,
)