package younesbouhouche.musicplayer.features.main.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.core.domain.models.Song
import kotlin.collections.sortedBy
import younesbouhouche.musicplayer.R


enum class PlaylistSortType(val label: Int, val icon: ImageVector, val sort: (List<Song>) -> List<Song>) {
    Custom(
        R.string.custom,
        Icons.Default.Person,
        { it }
    ),
    Title(
        R.string.title,
        Icons.Default.Title,
        { it.sortedBy { item -> item.title } }
    ),
    Filename(
        R.string.file_name,
        Icons.AutoMirrored.Default.InsertDriveFile,
        { it.sortedBy { item -> item.path } }
    ),
    Duration(
        R.string.duration,
        Icons.Default.Timer,
        { it.sortedBy { item -> item.duration } }
    ),
    Size(
        R.string.size,
        Icons.Default.Straighten,
        { it.sortedBy { item -> item.date } }
    ),
    Date(
        R.string.date_modified,
        Icons.Default.CalendarMonth,
        { it.sortedBy { item -> item.size } }
    ),
}