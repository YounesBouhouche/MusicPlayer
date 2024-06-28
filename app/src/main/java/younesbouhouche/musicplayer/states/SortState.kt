package younesbouhouche.musicplayer.states

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector

data class SortState(
    val sortType: SortType = SortType.Title,
    val expanded: Boolean = false,
    val ascending: Boolean = true
)

enum class SortType(val label: String, val icon: ImageVector) {
    Title("Title", Icons.Default.Title),
    Filename("File name", Icons.AutoMirrored.Default.InsertDriveFile),
    Duration("Duration", Icons.Default.Timer),
    Date("Date Modified", Icons.Default.CalendarMonth),
}

data class PlaylistSortState(
    val sortType: PlaylistSortType = PlaylistSortType.Custom,
    val expanded: Boolean = false,
    val ascending: Boolean = true
)

enum class PlaylistSortType(val label: String, val icon: ImageVector) {
    Custom("Custom", Icons.Default.Edit),
    Title("Title", Icons.Default.Title),
    Filename("File name", Icons.AutoMirrored.Default.InsertDriveFile),
    Duration("Duration", Icons.Default.Timer)
}