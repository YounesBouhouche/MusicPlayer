package younesbouhouche.musicplayer.ui.routes

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.launch
import sh.calvin.reorderable.rememberReorderableLazyListState
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.models.MusicCard
import younesbouhouche.musicplayer.events.PlaylistSortEvent
import younesbouhouche.musicplayer.isScrollingUp
import younesbouhouche.musicplayer.states.PlaylistSortState
import younesbouhouche.musicplayer.states.PlaylistSortType
import younesbouhouche.musicplayer.ui.components.LazyColumnWithPlaylistSortBar
import younesbouhouche.musicplayer.ui.components.SwipeMusicCardLazyItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    files: List<MusicCard>,
    title: String,
    sortState: PlaylistSortState,
    onSortEvent: (PlaylistSortEvent) -> Unit,
    navigateUp: () -> Unit,
    reorder: (Int, Int) -> Unit,
    onDismiss: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onClick: (Int) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val state = rememberLazyListState()
    val view = LocalView.current
    val reorderableState =
        if ((sortState.ascending) and (sortState.sortType == PlaylistSortType.Custom))
        rememberReorderableLazyListState(lazyListState = state) { from, to ->
            view.performHapticFeedback(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                else
                    HapticFeedbackConstants.GESTURE_END
            )
            reorder(from.index - 1, to.index - 1)
        }
        else null
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.isScrollingUp(),
                enter = materialSharedAxisZIn(true),
                exit = materialSharedAxisZOut(true)
            ) {
                FloatingActionButton(onClick = { onClick(0) }) {
                    Icon(Icons.Default.PlayArrow, null)
                }
            }
        }
    ) { paddingValues ->
        LazyColumnWithPlaylistSortBar(
            state = state,
            sortState = sortState,
            onSortEvent = onSortEvent,
            searchBarSpace = false,
            contentPadding = paddingValues) {
            items(files, { it.id }) { file ->
                val index = files.indexOf(file)
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        value == SwipeToDismissBoxValue.EndToStart
                    },
                    positionalThreshold = { it / 1.5f }
                )
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    onDismiss(index)
                    LaunchedEffect(Unit) {
                        launch { dismissState.reset() }
                    }
                }
                SwipeMusicCardLazyItem(
                    state = dismissState,
                    file = file,
                    reorderableState = reorderableState,
                    onLongClick = { onLongClick(index) }
                ) {
                    onClick(index)
                }
            }
        }
    }
}