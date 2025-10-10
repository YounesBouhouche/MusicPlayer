package younesbouhouche.musicplayer.main.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.util.getDurationRange
import younesbouhouche.musicplayer.main.util.getGroupingKey
import younesbouhouche.musicplayer.main.util.getSizeRange
import younesbouhouche.musicplayer.main.util.toReadableDate
import younesbouhouche.musicplayer.main.util.toReadableDurationString


enum class SortType(
    val label: Int,
    val icon: ImageVector,
    val sort: (List<MusicCard>) -> List<MusicCard>,
    val groupBy: (MusicCard) -> String,
) {
    Title(
        R.string.title,
        Icons.Default.Title,
        { it.sortedBy { item -> item.title } },
        { it.title.getGroupingKey() },
    ),
    Filename(
        R.string.file_name,
        Icons.AutoMirrored.Default.InsertDriveFile,
        { it.sortedBy { item -> item.fileName } },
        { it.fileName.getGroupingKey() },
    ),
    Duration(
        R.string.duration,
        Icons.Default.Timer,
        { it.sortedBy { item -> item.duration } },
        { it.duration.getDurationRange().toReadableDurationString() },
    ),
    Size(
        R.string.size,
        Icons.Default.Straighten,
        { it.sortedBy { item -> item.size } },
        { it.size.getSizeRange().toReadableDurationString() },
    ),
    Date(R.string.date_modified,
        Icons.Default.CalendarMonth,
        { it.sortedBy { item -> item.date } },
        { it.date.toReadableDate() },
    );
}
