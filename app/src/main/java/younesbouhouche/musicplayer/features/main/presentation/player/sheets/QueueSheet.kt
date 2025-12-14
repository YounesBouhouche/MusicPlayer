package younesbouhouche.musicplayer.features.main.presentation.player.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Queue
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.presentation.components.MusicCardListItem
import younesbouhouche.musicplayer.features.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.features.main.presentation.util.plus
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QueueSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    queue: Queue,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onSaveQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reorderState = rememberReorderState<Song>(true)
    val state = rememberModalBottomSheetState(true)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val exitAlwaysScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)
    var expanded by rememberSaveable { mutableStateOf(true) }
    var reorderedQueue by remember { mutableStateOf(queue.songs) }
    LaunchedEffect(queue.songs) {
        reorderedQueue = queue.songs
    }
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            sheetState = state,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            dragHandle = { BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.primary
            ) },
            contentWindowInsets = {
                BottomSheetDefaults.windowInsets.exclude(WindowInsets.navigationBars)
            }
        ) {
            Box(Modifier.fillMaxSize()) {
                Scaffold(
                    Modifier.fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .floatingToolbarVerticalNestedScroll(
                            expanded = expanded,
                            onExpand = { expanded = true },
                            onCollapse = { expanded = false },
                        ),
                    contentWindowInsets = WindowInsets(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    stringResource(R.string.next_up),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Medium,
                                )
                            },
                            expandedHeight = 80.dp,
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            ),
                            scrollBehavior = scrollBehavior
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) { paddingValues ->
                    ReorderContainer(reorderState) {
                        LazyColumn(
                            Modifier.fillMaxSize(),
                            contentPadding = paddingValues + PaddingValues(24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(reorderedQueue, { _, it -> it.id }) { index, item ->
                                ReorderableItem(
                                    modifier = Modifier.animateItem(),
                                    state = reorderState,
                                    key = item.id,
                                    data = item,
                                    onDrop = {
                                        val from = queue.songs.indexOf(it.data)
                                        val to = queue.songs.indexOf(item)
                                        reorderedQueue = reorderedQueue.toMutableList().apply {
                                            add(to, removeAt(from))
                                        }
                                        onPlayerEvent(PlayerEvent.Swap(from, to))
                                    }
                                ) {
                                    MusicCardListItem(
                                        item,
                                        active = index == queue.currentIndex,
                                        shape = expressiveRectShape(index, queue.songs.size),
                                        onDismiss = {
                                            onPlayerEvent(PlayerEvent.Remove(index))
                                        }
                                    ) {
                                        onPlayerEvent(PlayerEvent.Seek(index))
                                    }
                                }
                            }
                        }
                    }
                }
                HorizontalFloatingToolbar(
                    expanded,
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = -ScreenOffset),
                    scrollBehavior = exitAlwaysScrollBehavior,
                    floatingActionButton = {
                        FloatingToolbarDefaults.VibrantFloatingActionButton(onAddToPlaylist) {
                            Icon(Icons.AutoMirrored.Filled.PlaylistAdd, null)
                        }
                    }
                ) {
                    ExpressiveIconButton(
                        Icons.Default.ClearAll,
                        size = IconButtonDefaults.mediumIconSize
                    ) {
                        onPlayerEvent(PlayerEvent.Stop)
                    }
                    ExpressiveIconButton(
                        Icons.Default.Save,
                        size = IconButtonDefaults.mediumIconSize,
                        onClick = onSaveQueue
                    )
                }
            }
        }
    }
}