package younesbouhouche.musicplayer.main.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.ColsCount

@Serializable
data class SortState<T>(
    val sortType: T,
    val colsCount: ColsCount? = null,
    val expanded: Boolean = false,
    val ascending: Boolean = true,
)

enum class SortType(val label: Int, val icon: ImageVector) {
    Title(R.string.title, Icons.Default.Title),
    Filename(R.string.file_name, Icons.AutoMirrored.Default.InsertDriveFile),
    Duration(R.string.duration, Icons.Default.Timer),
    Size(R.string.size, Icons.Default.Straighten),
    Date(R.string.date_modified, Icons.Default.CalendarMonth),
}

enum class PlaylistSortType(val label: Int, val icon: ImageVector) {
    Custom(R.string.custom, Icons.Default.Person),
    Title(R.string.title, Icons.Default.Title),
    Filename(R.string.file_name, Icons.AutoMirrored.Default.InsertDriveFile),
    Duration(R.string.duration, Icons.Default.Timer),
    Size(R.string.size, Icons.Default.Straighten),
    Date(R.string.date_modified, Icons.Default.CalendarMonth),
}

enum class ListsSortType(val label: Int, val icon: ImageVector) {
    Name(R.string.title, Icons.Default.Title),
    Count(R.string.items_count, Icons.AutoMirrored.Default.List),
}