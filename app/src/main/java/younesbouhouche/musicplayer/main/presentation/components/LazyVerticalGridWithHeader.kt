package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import younesbouhouche.musicplayer.main.presentation.util.composables.isCompact
import younesbouhouche.musicplayer.main.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.main.presentation.util.composables.statusBarHeight
import younesbouhouche.musicplayer.main.presentation.util.plus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyVerticalGridWithHeader(
    columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    statusBarSpace: Boolean = true,
    searchBarSpace: Boolean = true,
    leadingContent: @Composable (LazyGridItemScope.() -> Unit)? = null,
    content: LazyGridScope.() -> Unit,
) {
    val isCompact = isCompact
    LazyVerticalGrid(
        columns,
        modifier,
        state,
        contentPadding + PaddingValues(
            top = (if (statusBarSpace) statusBarHeight else 0.dp)
                    + (if (searchBarSpace) SearchBarDefaults.InputFieldHeight + 24.dp else 0.dp),
            bottom = if (!isCompact) navBarHeight else 0.dp
        ),
        reverseLayout,
        verticalArrangement,
        horizontalArrangement,
        flingBehavior,
        userScrollEnabled,
    ) {
        leadingContent?.let {
            item(span = { GridItemSpan(maxLineSpan) }) {
                it(this)
            }
        }
        content()
    }
}
