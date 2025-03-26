package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut

@Composable
fun <T>ItemsLazyVerticalGrid(
    items: List<T>,
    itemKey: (T) -> Any,
    gridCount: Int,
    modifier: Modifier = Modifier,
    singleLineItemContent: @Composable (LazyGridItemScope.(T) -> Unit),
    itemContent: @Composable (LazyGridItemScope.(T) -> Unit),
) {
    var currentGridCount by remember { mutableIntStateOf(gridCount.coerceAtLeast(2)) }
    LaunchedEffect(gridCount) {
        if (gridCount > 1) currentGridCount = gridCount
    }
    AnimatedContent(
        targetState = gridCount == 1,
        label = "",
        transitionSpec = { materialSharedAxisZIn(true) togetherWith materialSharedAxisZOut(true) },
    ) { singleItem ->
        if (singleItem)
            LazyVerticalGridWithHeader(
                modifier = modifier,
                columns = GridCells.Fixed(1),
            ) {
                items(items, itemKey, itemContent = singleLineItemContent)
            }
        else
            LazyVerticalGridWithHeader(
                modifier = modifier,
                columns = GridCells.Fixed(currentGridCount),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items, itemKey, itemContent = itemContent)
            }
    }
}

@Composable
fun <T>ItemsLazyVerticalGrid(
    items: List<T>,
    itemKey: (T) -> Any,
    modifier: Modifier = Modifier,
    itemContent: @Composable (LazyGridItemScope.(T) -> Unit),
) = ItemsLazyVerticalGrid(items, itemKey, 1, modifier, itemContent, itemContent)