package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.presentation.components.EmptyContainer
import younesbouhouche.musicplayer.main.presentation.components.LazyMusicCardScreen
import younesbouhouche.musicplayer.main.presentation.components.SortSheet
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.main.presentation.util.sizeLabel
import younesbouhouche.musicplayer.main.presentation.util.timeLabel
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.io.path.name

@Composable
fun Library(
    files: List<MusicCard>,
    modifier: Modifier = Modifier,
    sortState: SortState<SortType>,
    onSortStateChange: (SortState<SortType>) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    play: (Int) -> Unit,
) {
    EmptyContainer(
        files.isEmpty(),
        Icons.Default.LibraryMusic,
        stringResource(R.string.empty_library),
        modifier,
    ) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp) + PaddingValues(top = 72.dp)
                    + WindowInsets.statusBars.asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            files.groupBy { it ->
                when(sortState.sortType) {
                    SortType.Title -> it.title.first().let { char ->
                        if (char.isDigit()) '#'
                        else if (!char.isLetter()) '@'
                        else char
                    }.toString()
                    SortType.Filename -> Paths.get(it.path).fileName.name.first().toString()
                    SortType.Duration -> it.duration.timeLabel
                    SortType.Size -> it.size.sizeLabel
                    SortType.Date -> LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it.date),
                        ZoneId.systemDefault(),
                    ).format(
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    )
                }
            }.forEach { header, group ->
                item {
                    Text(
                        text = header,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                itemsIndexed(group, { _, it -> it.id }) { index, it ->
                    LazyMusicCardScreen(
                        file = it,
                        shape = listItemShape(index, group.size),
                        background = MaterialTheme.colorScheme.surfaceContainerLow,
                        onLongClick = { onUiEvent(UiEvent.ShowBottomSheet(it.id)) },
                    ) {
                        play(files.indexOf(it))
                    }
                }
            }
        }
        SortSheet(sortState) { onSortStateChange(it) }
    }
}
