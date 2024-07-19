package younesbouhouche.musicplayer.main.presentation.routes

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.LazyVerticalGridWithSortBar
import younesbouhouche.musicplayer.core.presentation.MyListItem
import younesbouhouche.musicplayer.core.presentation.util.composables.isScrollingUp
import younesbouhouche.musicplayer.main.domain.events.ListsSortEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.Playlist
import younesbouhouche.musicplayer.main.presentation.states.ListSortState
import java.nio.file.Files
import java.nio.file.Paths

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Playlists(
    context: Context,
    playlists: List<Playlist>,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    sortState: ListSortState = ListSortState(),
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    onPlaylistsSortEvent: (ListsSortEvent) -> Unit,
) {
    val importLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) {
            it?.let { uri ->
                var name = ""
                val items = mutableListOf<String>()
                try {
                    context.contentResolver.openInputStream(uri)?.run {
                        (reader().readLines()).forEach { line ->
                            if (line.startsWith("#EXTINF:")) {
                                name = line.removePrefix("#EXTINF:").trim()
                            } else if (!line.startsWith("#EXTM3U") and Files.exists(Paths.get(line.trim()))) {
                                items.add(line.trim())
                            }
                        }
                        close()
                    }
                    if (name.isNotBlank() and items.isNotEmpty()) {
                        onPlaylistEvent(PlaylistEvent.CreateNewPlaylist(name, items))
                    }
                } catch (_: Exception) {
                }
            }
        }
    var gridCount by remember { mutableIntStateOf(2) }
    val state = rememberLazyListState()
    LaunchedEffect(key1 = sortState.colsCount.count) {
        if (sortState.colsCount.count > 1) gridCount = sortState.colsCount.count
    }
    Box(modifier.fillMaxSize()) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = sortState.colsCount.count == 1,
            label = "",
            transitionSpec = { materialSharedAxisZIn(true) togetherWith materialSharedAxisZOut(true) },
        ) { singleItem ->
            if (singleItem) {
                LazyVerticalGridWithSortBar(
                    modifier = Modifier,
                    columns = GridCells.Fixed(1),
                    sortState = sortState,
                    onSortEvent = onPlaylistsSortEvent,
                ) {
                    items(playlists, { it.id }) {
                        MyListItem(
                            onClick = { onClick(playlists.indexOf(it)) },
                            onLongClick = { onLongClick(playlists.indexOf(it)) },
                            headline = it.name,
                            supporting = pluralStringResource(R.plurals.item_s, it.items.size, it.items.size),
                            cover = null,
                            alternative = Icons.AutoMirrored.Default.PlaylistPlay,
                            modifier = Modifier.animateItem(),
                            trailingContent = {
                                IconButton(onClick = { onPlayerEvent(PlayerEvent.PlayPaths(it.items.toList())) }) {
                                    Icon(Icons.Outlined.PlayArrow, null)
                                }
                                IconButton(onClick = { onLongClick(playlists.indexOf(it)) }) {
                                    Icon(Icons.Default.MoreVert, null)
                                }
                            },
                        )
                    }
                }
            } else {
                LazyVerticalGridWithSortBar(
                    modifier = Modifier,
                    columns = GridCells.Fixed(gridCount),
                    sortState = sortState,
                    onSortEvent = onPlaylistsSortEvent,
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
                                    onLongClick = { onLongClick(playlists.indexOf(it)) },
                                )
                                .aspectRatio(1f)
                                .padding(8.dp),
                        ) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Default.PlaylistPlay,
                                    null,
                                    Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    it.name,
                                    Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            visible = state.isScrollingUp(),
            enter = materialSharedAxisZIn(true),
            exit = materialSharedAxisZOut(true),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(onClick = { importLauncher.launch("*/*") }) {
                    Icon(Icons.Default.FolderOpen, null)
                }
                FloatingActionButton(onClick = { onUiEvent(UiEvent.ShowCreatePlaylistDialog()) }) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
    }
}
