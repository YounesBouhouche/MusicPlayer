package younesbouhouche.musicplayer.states

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector

data class ListSortState(
    val sortType: ListsSortType = ListsSortType.Name,
    val expanded: Boolean = false,
    val ascending: Boolean = true,
    val colsCount: ColsCount = ColsCount.One,
    val colsExpanded: Boolean = false,
)

enum class ListsSortType(val label: String, val icon: ImageVector) {
    Name("Title", Icons.Default.Title),
    Count("Items count", Icons.AutoMirrored.Default.List)
}

enum class ColsCount(val count: Int, val label: String, val icon: ImageVector) {
    One(1, "One", Icons.AutoMirrored.Default.List),
    Two(2, "Two", Icons.Default.GridView),
    Three(3, "Three", Icons.Default.GridOn),
}