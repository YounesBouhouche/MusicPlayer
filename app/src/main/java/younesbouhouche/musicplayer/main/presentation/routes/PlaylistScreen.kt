package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.domain.models.UiPlaylist
import younesbouhouche.musicplayer.core.domain.models.getPictureRequest
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveButton
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.main.presentation.components.ListScreen
import younesbouhouche.musicplayer.main.presentation.components.MusicCardListItem
import younesbouhouche.musicplayer.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.main.presentation.util.topAppBarIconButtonColors

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlaylistScreen(
    playlist: UiPlaylist,
    smallPlayerExpanded: Boolean,
    sortState: SortState<PlaylistSortType>,
    onSortStateChange: (SortState<PlaylistSortType>) -> Unit,
    modifier: Modifier = Modifier,
    onShowBottomSheet: (MusicCard) -> Unit = {},
    onRemove: (index: Int) -> Unit = {},
    onReorder: (from: Int, to: Int) -> Unit = { _, _ -> },
    onPlay: (index: Int, shuffle: Boolean) -> Unit = { _, _ -> },
) {
    val buttonSize = ButtonDefaults.MediumContainerHeight
    var reorderedPlaylist by remember { mutableStateOf(playlist) }
    val hapticFeedback = LocalHapticFeedback.current
    val state = rememberLazyListState()
    val reorderState = rememberReorderState<MusicCard>(true)
    val enableReorder = sortState.ascending and (sortState.sortType == PlaylistSortType.Custom)
//    LaunchedEffect(playlist) {
//        reorderedPlaylist = playlist
//    }
    val bottomPadding by animateDpAsState(
        if (smallPlayerExpanded) 220.dp else 128.dp
    )
    Box(modifier.fillMaxSize()) {
        ReorderContainer(reorderState, enabled = enableReorder) {
            ListScreen(
                playlist.name,
                reorderedPlaylist.items,
                reorderedPlaylist.getPictureRequest(),
                Icons.AutoMirrored.Filled.PlaylistPlay,
                sortState,
                onSortStateChange,
                PlaylistSortType.entries,
                {
                    it.icon
                },
                {
                    it.label
                },
                modifier,
                iconShape = MaterialShapes.Cookie4Sided.toShape(),
                actions = {
                    ExpressiveIconButton(
                        Icons.Default.Edit,
                        size = IconButtonDefaults.mediumIconSize,
                        colors = topAppBarIconButtonColors()
                    ) {

                    }
                },
                onShowBottomSheet = onShowBottomSheet,
                listState = state,
                contentPadding = PaddingValues(bottom = bottomPadding + buttonSize + 16.dp),
                onPlay = onPlay
            ) { index, card ->
                ReorderableItem(
                    state = reorderState,
                    key = card.id,
                    data = card,
                    enabled = enableReorder,
                    onDragEnter = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                    },
                    onDrop = { state ->
                        val from = reorderedPlaylist.items.indexOf(state.data)
                        val to = reorderedPlaylist.items.indexOf(card)
                        reorderedPlaylist = reorderedPlaylist.copy(
                            items = reorderedPlaylist.items.toMutableList().apply {
                                add(to, removeAt(from))
                            }
                        )
                        onReorder(from, to)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                    }
                ) {
                    MusicCardListItem(
                        card,
                        shape = expressiveRectShape(index, playlist.items.size),
                        modifier = Modifier.animateItem(),
                        onDismiss = {
                            onRemove(index)
                        },
                        leadingContent = {
                            AnimatedVisibility(
                                enableReorder,
                                enter = expandHorizontally(expandFrom = Alignment.Start),
                                exit = shrinkHorizontally(shrinkTowards = Alignment.Start),
                            ) {
                                Icon(
                                    Icons.Default.DragIndicator,
                                    null,
                                    Modifier.size(IconButtonDefaults.mediumIconSize),
                                )
                            }
                        },
                        trailingContent = {
                            ExpressiveIconButton(
                                Icons.Default.MoreVert,
                                widthOption = IconButtonDefaults.IconButtonWidthOption.Narrow,
                                size = IconButtonDefaults.mediumIconSize,
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            ) {
                                onShowBottomSheet(card)
                            }
                        },
                    ) {
                        onPlay(index, false)
                    }
                }
            }
        }
        ExpressiveButton(
            stringResource(R.string.add_items),
            buttonSize,
            Modifier.align(Alignment.BottomCenter).padding(bottom = bottomPadding),
            Icons.Default.Add,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        ) {

        }
    }
}