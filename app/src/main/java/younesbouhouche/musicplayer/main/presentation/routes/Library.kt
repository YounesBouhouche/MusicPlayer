package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import younesbouhouche.musicplayer.core.presentation.LazyColumnWithSortBar
import younesbouhouche.musicplayer.core.presentation.LazyMusicCardScreen
import younesbouhouche.musicplayer.core.presentation.util.composables.isCompact
import younesbouhouche.musicplayer.core.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.main.domain.events.SortEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.SortState

@Composable
fun Library(
    files: List<MusicCard>,
    modifier: Modifier = Modifier,
    sortState: SortState,
    onSortEvent: (SortEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    play: (Int) -> Unit,
) {
    val isCompact = isCompact
    LazyColumnWithSortBar(
        modifier = modifier,
        sortState = sortState,
        onSortEvent = onSortEvent,
    ) {
        items(files, { it.id }) {
            LazyMusicCardScreen(
                file = it,
                onLongClick = { onUiEvent(UiEvent.ShowBottomSheet(it)) },
            ) {
                play(files.indexOf(it))
            }
        }
        if (!isCompact) item { Spacer(Modifier.height(navBarHeight)) }
    }
}
