package younesbouhouche.musicplayer.main.presentation.states

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.R

data class SortState(
    val sortType: SortType = SortType.Title,
    val expanded: Boolean = false,
    val ascending: Boolean = true,
)

enum class SortType(val label: Int, val icon: ImageVector) {
    Title(R.string.title, Icons.Default.Title),
    Filename(R.string.file_name, Icons.AutoMirrored.Default.InsertDriveFile),
    Duration(R.string.duration, Icons.Default.Timer),
    Date(R.string.date_modified, Icons.Default.CalendarMonth),
}

data class PlaylistSortState(
    val sortType: PlaylistSortType = PlaylistSortType.Custom,
    val expanded: Boolean = false,
    val ascending: Boolean = true,
)

enum class PlaylistSortType(val label: Int, val icon: ImageVector) {
    Custom(R.string.custom, Icons.Default.Edit),
    Title(R.string.title, Icons.Default.Title),
    Filename(R.string.file_name, Icons.AutoMirrored.Default.InsertDriveFile),
    Duration(R.string.duration, Icons.Default.Timer),
}
