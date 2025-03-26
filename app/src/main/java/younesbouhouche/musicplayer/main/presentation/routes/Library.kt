package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.components.ItemsLazyVerticalGrid
import younesbouhouche.musicplayer.main.presentation.components.LazyMusicCardScreen
import younesbouhouche.musicplayer.main.presentation.components.SortSheet
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType

@Composable
fun Library(
    files: List<MusicCard>,
    modifier: Modifier = Modifier,
    sortState: SortState<SortType>,
    onSortStateChange: (SortState<SortType>) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    play: (Int) -> Unit,
) {
    ItemsLazyVerticalGrid(files, { it.id }, modifier) {
        LazyMusicCardScreen(
            file = it,
            onLongClick = { onUiEvent(UiEvent.ShowBottomSheet(it)) },
        ) {
            play(files.indexOf(it))
        }
    }
    SortSheet(sortState) { onSortStateChange(it) }
}
