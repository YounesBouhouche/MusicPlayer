package younesbouhouche.musicplayer.core.domain.models

import android.graphics.Bitmap

data class Artist(
    val name: String = "",
    val items: List<Long> = emptyList(),
    var cover: Bitmap? = null,
)
