package younesbouhouche.musicplayer.ui.components

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.events.ListsSortEvent
import younesbouhouche.musicplayer.states.ListSortState
import younesbouhouche.musicplayer.ui.statusBarHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyVerticalGridWithHeader(
    columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement. Vertical = if (!reverseLayout) Arrangement. Top else Arrangement. Bottom,
    horizontalArrangement: Arrangement. Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    searchBarSpace: Boolean = true,
    leadingContent: @Composable (LazyGridItemScope.() -> Unit),
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGrid(
        columns, modifier, state, contentPadding, reverseLayout, verticalArrangement, horizontalArrangement, flingBehavior, userScrollEnabled
    ) {
        if (searchBarSpace)
            item {
                Spacer(
                    Modifier
                        .height(
                            statusBarHeight +
                                    SearchBarDefaults.InputFieldHeight +
                                    16.dp
                        )
                )
            }
        item(span = { GridItemSpan(maxLineSpan) }) {
            leadingContent(this)
        }
        content()
    }
}

@Composable
fun LazyVerticalGridWithSortBar(
    columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement. Vertical = if (!reverseLayout) Arrangement. Top else Arrangement. Bottom,
    horizontalArrangement: Arrangement. Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    searchBarSpace: Boolean = true,
    sortState: ListSortState,
    onSortEvent: (ListsSortEvent) -> Unit,
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGridWithHeader(
        columns,
        modifier,
        state,
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalArrangement,
        flingBehavior,
        userScrollEnabled,
        searchBarSpace,
        {
            ListsSortBar(Modifier.animateItem(), sortState, onSortEvent)
        },
        content
    )
}