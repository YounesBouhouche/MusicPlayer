package younesbouhouche.musicplayer.features.main.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.util.getDurationRange
import younesbouhouche.musicplayer.features.main.util.getGroupingKey
import younesbouhouche.musicplayer.features.main.util.getSizeRange
import younesbouhouche.musicplayer.features.main.util.toReadableDate
import younesbouhouche.musicplayer.features.main.util.toReadableDurationString


enum class SortType(
    val label: Int,
    val icon: ImageVector,
    val sort: (List<Song>) -> List<Song>,
    val groupBy: (Song) -> String,
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
    Date(
        R.string.date_modified,
        Icons.Default.CalendarMonth,
        { it.sortedBy { item -> item.date } },
        { it.date.toReadableDate() },
    ),
    Artist(
        R.string.artist,
        Icons.Default.Person,
        { it.sortedBy { item -> item.artist } },
        { it.artist.getGroupingKey() },
    ),
    Album(
        R.string.album,
        Icons.Default.Album,
        { it.sortedBy { item -> item.album } },
        { it.album.getGroupingKey() },
    ),
    Year(
        R.string.year,
        Icons.Default.CalendarViewMonth,
        { it.sortedBy { item -> item.year ?: 0 } },
        { item -> item.year?.toString() ?: "Unknown" },
    ),
    TrackNumber(
        R.string.track_number,
        Icons.Default.Numbers,
        { it.sortedBy { item -> item.trackNumber ?: 0 } },
        { item -> item.trackNumber?.toString() ?: "Unknown" },
    ),
    DiscNumber(
        R.string.disc_number,
        Icons.Default.Numbers,
        { it.sortedBy { item -> item.discNumber ?: 0 } },
        { item -> item.discNumber?.toString() ?: "Unknown" },
    );
}
