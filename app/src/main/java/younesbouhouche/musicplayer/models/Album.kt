package younesbouhouche.musicplayer.models

import android.graphics.Bitmap

data class Album(
    val title: String = "",
    val items: List<Long> = emptyList(),
    var cover: Bitmap? = null,
)
