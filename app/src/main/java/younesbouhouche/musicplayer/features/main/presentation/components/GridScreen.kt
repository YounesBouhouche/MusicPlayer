package younesbouhouche.musicplayer.features.main.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import younesbouhouche.musicplayer.features.main.presentation.ColsCount
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.presentation.util.plus

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun<T, S> GridScreen(
    items: List<T>,
    sortState: SortState<S>,
    onExpandSortSheet: () -> Unit,
    lineContent: @Composable LazyItemScope.(T) -> Unit,
    gridContent: @Composable LazyGridItemScope.(T) -> Unit,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(),
) {
    var colsCount by remember {
        mutableIntStateOf(sortState.colsCount?.count ?: 2)
    }
    LaunchedEffect(sortState.colsCount) {
        sortState.colsCount?.count?.takeIf { it > 1 }?.let {
            colsCount = it
        }
    }
    val sortButton = @Composable {
        Row(
            Modifier.padding(vertical = 16.dp, horizontal = 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            ExpressiveIconButton(
                icon = Icons.AutoMirrored.Filled.Sort,
                onClick = onExpandSortSheet,
                size = IconButtonDefaults.mediumIconSize,
                colors = IconButtonDefaults.filledTonalIconButtonColors(),
            )
        }
    }
    AnimatedContent(sortState.colsCount == ColsCount.One, Modifier.fillMaxSize()) {
        if (it) {
            LazyColumn(
                Modifier.fillMaxSize().padding(horizontal = 8.dp).then(modifier),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = contentPadding
            ) {
                item {
                    sortButton()
                }
                items(items, key) { item ->
                    lineContent(item)
                }
            }
        } else {
            LazyVerticalGrid(
                GridCells.Fixed(colsCount),
                Modifier.fillMaxSize().padding(horizontal = 8.dp).then(modifier),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = contentPadding
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    sortButton()
                }
                items(items, key) { item ->
                    gridContent(item)
                }
            }
        }
    }
}