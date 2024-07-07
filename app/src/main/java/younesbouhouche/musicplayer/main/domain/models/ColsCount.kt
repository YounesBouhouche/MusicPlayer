package younesbouhouche.musicplayer.main.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.ui.graphics.vector.ImageVector

enum class ColsCount(val count: Int, val label: String, val icon: ImageVector) {
    One(1, "One", Icons.AutoMirrored.Default.List),
    Two(2, "Two", Icons.Default.GridView),
    Three(3, "Three", Icons.Default.GridOn),
}