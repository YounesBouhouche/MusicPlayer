package younesbouhouche.musicplayer.core.presentation

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.core.presentation.util.composables.statusBarHeight
import younesbouhouche.musicplayer.main.domain.events.PlaylistSortEvent
import younesbouhouche.musicplayer.main.domain.events.SortEvent
import younesbouhouche.musicplayer.main.presentation.states.PlaylistSortState
import younesbouhouche.musicplayer.main.presentation.states.SortState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyColumnWithHeader(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    searchBarSpace: Boolean = true,
    leadingContent: @Composable (LazyItemScope.() -> Unit),
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier,
        state,
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalAlignment,
        flingBehavior,
        userScrollEnabled,
    ) {
        if (searchBarSpace) {
            item {
                Spacer(
                    Modifier
                        .height(
                            statusBarHeight +
                                SearchBarDefaults.InputFieldHeight +
                                16.dp,
                        ),
                )
            }
        }
        item {
            leadingContent()
        }
        content()
    }
}

@Composable
fun LazyColumnWithSortBar(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    searchBarSpace: Boolean = true,
    sortState: SortState,
    onSortEvent: (SortEvent) -> Unit,
    content: LazyListScope.() -> Unit,
) {
    LazyColumnWithHeader(
        modifier,
        state,
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalAlignment,
        flingBehavior,
        userScrollEnabled,
        searchBarSpace,
        {
            SortBar(Modifier.animateItem(), sortState, onSortEvent)
        },
        content,
    )
}

@Composable
fun LazyColumnWithPlaylistSortBar(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    searchBarSpace: Boolean = true,
    sortState: PlaylistSortState,
    onSortEvent: (PlaylistSortEvent) -> Unit,
    content: LazyListScope.() -> Unit,
) {
    LazyColumnWithHeader(
        modifier,
        state,
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalAlignment,
        flingBehavior,
        userScrollEnabled,
        searchBarSpace,
        {
            PlaylistSortBar(Modifier.animateItem(), sortState, onSortEvent)
        },
        content,
    )
}
