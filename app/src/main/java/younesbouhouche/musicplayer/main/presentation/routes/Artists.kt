package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.core.presentation.LazyVerticalGridWithSortBar
import younesbouhouche.musicplayer.core.presentation.MyCard
import younesbouhouche.musicplayer.core.presentation.MyListItem
import younesbouhouche.musicplayer.main.domain.events.ListsSortEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.models.Artist
import younesbouhouche.musicplayer.main.presentation.states.ListSortState

@Composable
fun Artists(
    artists: List<Artist>,
    onClick: (Artist) -> Unit,
    onLongClick: (Artist) -> Unit,
    modifier: Modifier = Modifier,
    sortState: ListSortState = ListSortState(),
    onPlayerEvent: (PlayerEvent) -> Unit,
    onArtistsSortEvent: (ListsSortEvent) -> Unit,
) {
    var gridCount by remember { mutableIntStateOf(2) }
    LaunchedEffect(key1 = sortState.colsCount.count) {
        if (sortState.colsCount.count > 1) gridCount = sortState.colsCount.count
    }
    AnimatedContent(
        targetState = sortState.colsCount.count == 1,
        label = "",
        transitionSpec = { materialSharedAxisZIn(true) togetherWith materialSharedAxisZOut(true) },
    ) { singleItem ->
        if (singleItem) {
            LazyVerticalGridWithSortBar(
                modifier = modifier,
                columns = GridCells.Fixed(1),
                sortState = sortState,
                onSortEvent = onArtistsSortEvent,
            ) {
                items(artists.toList(), { it.name }) {
                    MyListItem(
                        onClick = { onClick(it) },
                        onLongClick = { onLongClick(it) },
                        headline = it.name,
                        supporting = "${it.items.size} items",
                        cover = it.cover?.asImageBitmap(),
                        alternative = Icons.Default.AccountCircle,
                        modifier = Modifier.animateItem(),
                        trailingContent = {
                            IconButton(onClick = { onPlayerEvent(PlayerEvent.PlayIds(it.items)) }) {
                                Icon(Icons.Outlined.PlayArrow, null)
                            }
                            IconButton(onClick = { onLongClick(it) }) {
                                Icon(Icons.Default.MoreVert, null)
                            }
                        },
                    )
                }
            }
        } else {
            LazyVerticalGridWithSortBar(
                modifier = modifier,
                columns = GridCells.Fixed(gridCount),
                sortState = sortState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                onSortEvent = onArtistsSortEvent,
            ) {
                items(artists.toList(), { it.name }) {
                    MyCard(
                        modifier = Modifier.animateItem(),
                        text = it.name,
                        cover = it.cover,
                        alternative = Icons.Default.AccountCircle,
                        onClick = { onClick(it) },
                        onLongClick = { onLongClick(it) },
                    )
                }
            }
        }
    }
}
