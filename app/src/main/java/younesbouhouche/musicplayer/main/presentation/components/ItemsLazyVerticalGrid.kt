package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
    itemKey: (Int, T) -> Any,
    gridCount: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    singleLineItemContent: @Composable (LazyGridItemScope.(Int, T) -> Unit),
    itemContent: @Composable (LazyGridItemScope.(Int, T) -> Unit),
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
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement
            ) {
                itemsIndexed(items, itemKey, itemContent = singleLineItemContent)
            }
        else
            LazyVerticalGridWithHeader(
                modifier = modifier,
                columns = GridCells.Fixed(currentGridCount),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(items, itemKey, itemContent = itemContent)
            }
    }
}

@Composable
fun <T>ItemsLazyVerticalGrid(
    items: List<T>,
    itemKey: (Int, T) -> Any,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemContent: @Composable (LazyGridItemScope.(Int, T) -> Unit),
) = ItemsLazyVerticalGrid(
    items,
    itemKey,
    1,
    modifier,
    contentPadding,
    verticalArrangement,
    itemContent,
    itemContent
)