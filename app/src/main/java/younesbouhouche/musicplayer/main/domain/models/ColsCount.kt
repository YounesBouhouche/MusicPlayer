package younesbouhouche.musicplayer.main.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.R

enum class ColsCount(val count: Int, val label: Int, val icon: ImageVector) {
    One(1, R.string.one, Icons.AutoMirrored.Default.List),
    Two(2, R.string.two, Icons.Default.GridView),
    Three(3, R.string.three, Icons.Default.GridOn),
}
