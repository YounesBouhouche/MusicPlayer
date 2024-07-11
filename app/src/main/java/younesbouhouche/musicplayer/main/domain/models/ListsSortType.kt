package younesbouhouche.musicplayer.main.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.R

enum class ListsSortType(val label: Int, val icon: ImageVector) {
    Name(R.string.title, Icons.Default.Title),
    Count(R.string.items_count, Icons.AutoMirrored.Default.List),
}
