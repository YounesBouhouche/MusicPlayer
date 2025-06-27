package younesbouhouche.musicplayer.main.presentation.routes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.presentation.components.EmptyContainer
import younesbouhouche.musicplayer.main.presentation.components.ItemsLazyVerticalGrid
import younesbouhouche.musicplayer.main.presentation.components.ListsSortSheet
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import younesbouhouche.musicplayer.main.presentation.components.PlaylistListItem
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.composables.isScrollingUp
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.Playlists(
    playlists: List<Playlist>,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    sortState: SortState<ListsSortType>,
    onSortStateChange: (SortState<ListsSortType>) -> Unit,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
) {
    val context = LocalContext.current
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
                        onPlaylistEvent(PlaylistEvent.CreateNew(name, items, null))
                    }
                } catch (_: Exception) {
                }
            }
        }
    val state = rememberLazyListState()
    Box(modifier.fillMaxSize()) {
        EmptyContainer(
            playlists.isEmpty(),
            Icons.AutoMirrored.Filled.PlaylistPlay,
            stringResource(R.string.empty_playlists)
        ) {
            ItemsLazyVerticalGrid(
                items = playlists,
                itemKey = { _, it -> it.id },
                gridCount = sortState.colsCount?.count ?: 1,
                modifier = modifier,
                contentPadding = PaddingValues(12.dp),
                singleLineItemContent = { index, it ->
                    PlaylistListItem(
                        it,
                        animatedContentScope,
                        { onClick(it.id) },
                        { onLongClick(it.id) },
                        { onPlayerEvent(PlayerEvent.PlayPlaylist(it.id)) },
                        Modifier.animateItem(),
                        shape = listItemShape(index, playlists.size),
                        background = MaterialTheme.colorScheme.surfaceContainerLow,
                    )
                },
                itemContent = { _, it ->
                    Box(
                        Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.extraLarge)
                            .clipToBounds()
                            .combinedClickable(
                                onClick = { onClick(it.id) },
                                onLongClick = { onLongClick(playlists.indexOf(it)) },
                            )
                            .padding(8.dp),
                    ) {
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            MyImage(
                                it.image?.let { image -> File(context.filesDir, image) },
                                Icons.AutoMirrored.Default.PlaylistPlay,
                                Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = "playlist-${it.id}"),
                                        animatedVisibilityScope = animatedContentScope
                                    )
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                shape = MaterialTheme.shapes.large
                            )
                            Text(
                                it.name,
                                Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            )
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
    ListsSortSheet(sortState) { onSortStateChange(it) }
}
