package younesbouhouche.musicplayer.ui.routes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZ
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.ListsSortEvent
import younesbouhouche.musicplayer.MusicCard
import younesbouhouche.musicplayer.PlayerEvent
import younesbouhouche.musicplayer.states.ListSortState
import younesbouhouche.musicplayer.ui.components.LazyVerticalGridWithSortBar
import younesbouhouche.musicplayer.ui.components.MyListItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Albums(
    albums: List<Pair<String, List<MusicCard>>>,
    onClick: (Pair<String, List<MusicCard>>) -> Unit,
    onLongClick: (Pair<String, List<MusicCard>>) -> Unit,
    modifier: Modifier = Modifier,
    sortState: ListSortState = ListSortState(),
    onPlayerEvent: (PlayerEvent) -> Unit,
    onAlbumsSortEvent: (ListsSortEvent) -> Unit
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
        if (singleItem)
            LazyVerticalGridWithSortBar(
                modifier = modifier,
                columns = GridCells.Fixed(1),
                sortState = sortState,
                onSortEvent = onAlbumsSortEvent
            ) {
                items(albums.toList(), { it.first }) {
                    MyListItem(
                        onClick = { onClick(it) },
                        onLongClick = { onLongClick(it) },
                        headline = it.first,
                        supporting = "${it.second.size} items",
                        cover = null,
                        alternative = Icons.Default.Album,
                        trailingContent = {
                            IconButton(onClick = { onPlayerEvent(PlayerEvent.Play(it.second))  }) {
                                Icon(Icons.Outlined.PlayArrow, null)
                            }
                            IconButton(onClick = { onLongClick(it) }) {
                                Icon(Icons.Default.MoreVert, null)
                            }
                        }
                    )
                }
            }
        else
            LazyVerticalGridWithSortBar(
                modifier = modifier,
                columns = GridCells.Fixed(gridCount),
                sortState = sortState,
                onSortEvent = onAlbumsSortEvent
            ) {
                items(albums.toList(), { it.first }) {
                    Box(
                        Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.extraLarge)
                            .clipToBounds()
                            .combinedClickable(
                                onClick = { onClick(it) },
                                onLongClick = { onLongClick(it) }
                            )
                            .aspectRatio(1f)
                            .padding(8.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(4.dp)) {
                            Icon(
                                Icons.Default.Album,
                                null,
                                Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                it.first,
                                Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
    }
}