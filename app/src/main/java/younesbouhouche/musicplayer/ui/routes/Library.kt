package younesbouhouche.musicplayer.ui.routes

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import younesbouhouche.musicplayer.MusicCard
import younesbouhouche.musicplayer.SortEvent
import younesbouhouche.musicplayer.UiEvent
import younesbouhouche.musicplayer.states.SortState
import younesbouhouche.musicplayer.states.UiState
import younesbouhouche.musicplayer.ui.components.LazyColumnWithSortBar
import younesbouhouche.musicplayer.ui.components.MusicCardLazyItem

@Composable
fun Library(
    files: List<MusicCard>,
    modifier: Modifier = Modifier,
    sortState: SortState,
    onSortEvent: (SortEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    play: (Int) -> Unit
) {
    LazyColumnWithSortBar(
        modifier = modifier,
        sortState = sortState,
        onSortEvent = onSortEvent
    ) {
        items(files, { it.id }) {
            MusicCardLazyItem(file = it, onLongClick = { onUiEvent(UiEvent.ShowBottomSheet(it)) }) {
                play(files.indexOf(it))
            }
        }
    }
}