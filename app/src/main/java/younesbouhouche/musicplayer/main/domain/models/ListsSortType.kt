package younesbouhouche.musicplayer.main.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector

enum class ListsSortType(val label: String, val icon: ImageVector) {
    Name("Title", Icons.Default.Title),
    Count("Items count", Icons.AutoMirrored.Default.List),
}
