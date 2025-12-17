package younesbouhouche.musicplayer.features.main.presentation.routes.playlist

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import com.younesb.mydesignsystem.presentation.components.ExpressiveButton
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.core.domain.models.getPictureRequest
import younesbouhouche.musicplayer.features.main.presentation.components.ListScreen
import younesbouhouche.musicplayer.features.main.presentation.components.SongListItem
import younesbouhouche.musicplayer.features.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.features.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.features.main.presentation.util.topAppBarIconButtonColors

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlaylistScreen(
    playlistId: Long,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    onShowBottomSheet: (Song) -> Unit = {},
) {
    val viewModel: PlaylistViewModel = koinViewModel(
        parameters = { parametersOf(playlistId) }
    )
    val playlist by viewModel.playlist.collectAsStateWithLifecycle()
    val sortState by viewModel.sortState.collectAsStateWithLifecycle()
    val buttonSize = ButtonDefaults.MediumContainerHeight
    var tempPlaylist by remember { mutableStateOf(playlist) }
    val hapticFeedback = LocalHapticFeedback.current
    val state = rememberLazyListState()
    val reorderState = rememberReorderState<Song>(true)
    val enableReorder = sortState.ascending and (sortState.sortType == PlaylistSortType.Custom)
    val updatePlaylist: ((MutableList<Song>) -> List<Song>) -> Unit = { callback ->
        val newList = callback(tempPlaylist.songs.toMutableList())
        tempPlaylist = tempPlaylist.copy(songs = newList)
        viewModel.update(newList.map { it.id })
    }
    LaunchedEffect(playlist) {
        tempPlaylist = playlist
    }
    Box(modifier.fillMaxSize()) {
        ReorderContainer(reorderState, enabled = enableReorder) {
            ListScreen(
                playlist.name,
                tempPlaylist.songs,
                tempPlaylist.getPictureRequest(),
                Icons.AutoMirrored.Filled.PlaylistPlay,
                sortState,
                viewModel::setSortState,
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
                onPlay = { songs, index, shuffle ->
                    viewModel.play(songs.map { it.id }, index, shuffle)
                }
            ) { items, index, card ->
                ReorderableItem(
                    state = reorderState,
                    key = card.id,
                    data = card,
                    enabled = enableReorder,
                    onDragEnter = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                    },
                    onDrop = { state ->
                        val from = tempPlaylist.songs.indexOf(state.data)
                        val to = tempPlaylist.songs.indexOf(card)
                        updatePlaylist {
                            it.apply {
                                add(to, removeAt(from))
                            }
                        }
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                    },
                    modifier = Modifier.animateItem(),
                ) {
                    SongListItem(
                        card,
                        shape = expressiveRectShape(index, playlist.songs.size),
                        onDismiss = {
                            updatePlaylist {
                                it.apply {
                                    removeAt(index)
                                }
                            }
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
                        viewModel.play(items.map { it.id }, index, false)
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