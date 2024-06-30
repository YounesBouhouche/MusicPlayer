package younesbouhouche.musicplayer.ui.routes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.events.ListsSortEvent
import younesbouhouche.musicplayer.events.PlayerEvent
import younesbouhouche.musicplayer.models.Playlist
import younesbouhouche.musicplayer.events.UiEvent
import younesbouhouche.musicplayer.isScrollingUp
import younesbouhouche.musicplayer.states.ListSortState
import younesbouhouche.musicplayer.ui.components.LazyVerticalGridWithSortBar
import younesbouhouche.musicplayer.ui.components.MyListItem


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Playlists(
    playlists: List<Playlist>,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    sortState: ListSortState = ListSortState(),
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    onPlaylistsSortEvent: (ListsSortEvent) -> Unit
) {
    var gridCount by remember { mutableIntStateOf(2) }
    val state = rememberLazyListState()
    LaunchedEffect(key1 = sortState.colsCount.count) {
        if (sortState.colsCount.count > 1) gridCount = sortState.colsCount.count
    }
    Scaffold(
        Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.isScrollingUp(),
                enter = materialSharedAxisZIn(true),
                exit = materialSharedAxisZOut(true)
            ) {
                FloatingActionButton(onClick = { onUiEvent(UiEvent.ShowNewPlaylistDialog) }) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }) { paddingValues ->
        AnimatedContent(
            modifier = Modifier.padding(paddingValues),
            targetState = sortState.colsCount.count == 1,
            label = "",
            transitionSpec = { materialSharedAxisZIn(true) togetherWith materialSharedAxisZOut(true) },
        ) { singleItem ->
            if (singleItem)
                LazyVerticalGridWithSortBar(
                    modifier = modifier,
                    columns = GridCells.Fixed(1),
                    sortState = sortState,
                    onSortEvent = onPlaylistsSortEvent
                ) {
                    items(playlists, { it.id }) {
                        MyListItem(
                            onClick = { onClick(playlists.indexOf(it)) },
                            onLongClick = { onLongClick(playlists.indexOf(it)) },
                            headline = it.name,
                            supporting = "${it.items.size} items",
                            cover = null,
                            alternative = Icons.AutoMirrored.Default.PlaylistPlay,
                            trailingContent = {
                                IconButton(onClick = { onPlayerEvent(PlayerEvent.PlayPaths(it.items.toList()))  }) {
                                    Icon(Icons.Outlined.PlayArrow, null)
                                }
                                IconButton(onClick = { onLongClick(playlists.indexOf(it)) }) {
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
                    onSortEvent = onPlaylistsSortEvent
                ) {
                    items(playlists.toList(), { it.id }) {
                        Box(
                            Modifier
                                .animateItem()
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.extraLarge)
                                .clipToBounds()
                                .combinedClickable(
                                    onClick = { onClick(playlists.indexOf(it)) },
                                    onLongClick = { onLongClick(playlists.indexOf(it)) }
                                )
                                .aspectRatio(1f)
                                .padding(8.dp)) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)) {
                                Icon(
                                    Icons.AutoMirrored.Default.PlaylistPlay,
                                    null,
                                    Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    it.name,
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
}