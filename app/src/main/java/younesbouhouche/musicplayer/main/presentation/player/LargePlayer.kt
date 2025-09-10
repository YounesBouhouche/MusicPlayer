package younesbouhouche.musicplayer.main.presentation.player

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.QueueModel
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.states.PlaylistViewState
import younesbouhouche.musicplayer.main.presentation.states.UiState
import younesbouhouche.musicplayer.main.presentation.util.composables.isCompact
import younesbouhouche.musicplayer.main.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.main.presentation.util.composables.toDp
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LargePlayer(
    expanded: Boolean,
    queue: QueueModel,
    playerState: PlayerState,
    uiState: UiState,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onPlayerEvent: (PlayerEvent) -> Unit,
    lyrics: Boolean,
    syncing: Boolean,
    onUiEvent: (UiEvent) -> Unit,
    playlistState: AnchoredDraggableState<PlaylistViewState>,
    playlistProgress: Float,
    playlistDragEnabled: Boolean,
    modifier: Modifier = Modifier,
) {

    val scope = rememberCoroutineScope()
    val activeItem = queue.items[queue.index]
    val pagerState = rememberPagerState(queue.index) { queue.items.count() }
    val navBarHeight = navBarHeight
    var height by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val offset =
        with(density) {
            -((height - 72.dp.roundToPx()) * playlistProgress).roundToInt()
        }
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    var isScrolledByUser by remember { mutableStateOf(false) }
    val playing = (playerState.playState == PlayState.PLAYING) or playerState.loading
    val context = LocalContext.current
    val playerDataStore = PlayerDataStore(context)
    val showVolumeSlider by playerDataStore.showVolumeSlider.collectAsState(initial = false)
    val maxWidth = LocalView.current.width.toDp()
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }.collect { isScrolling ->
            if (isScrolledByUser && !isScrolling) {
                if (pagerState.settledPage != queue.index) {
                    onPlaybackEvent(PlaybackEvent.Seek(pagerState.settledPage, 0))
                }
            }
            isScrolledByUser = isScrolling && isDragged
        }
    }
    LaunchedEffect(key1 = queue.index) {
        launch { pagerState.animateScrollToPage(queue.index) }
    }
    val containerModifier =
        Modifier
            .offset { IntOffset(0, offset) }
            .onGloballyPositioned { height = it.size.height }
            .padding(bottom = 80.dp + navBarHeight)
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background,
                RoundedCornerShape(bottomEnd = 60.dp, bottomStart = 60.dp),
            )
            .clip(RoundedCornerShape(bottomEnd = 60.dp, bottomStart = 60.dp))
            .clipToBounds()
    Box(modifier) {
        if (isCompact) {
            Column(
                containerModifier.statusBarsPadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Pager(
                    expanded,
                    lyrics,
                    syncing,
                    playing,
                    pagerState,
                    queue,
                    playerState.time,
                    onPlaybackEvent,
                    onUiEvent,
                    Modifier
                        .weight(1f)
                        .sizeIn(maxHeight = maxWidth, maxWidth = maxWidth)
                        .aspectRatio(1f, true)
                ) { path, favorite ->
                    onPlayerEvent(PlayerEvent.UpdateFavorite(path, favorite))
                }
                Spacer(Modifier.height(8.dp))
                Controls(
                    activeItem,
                    showVolumeSlider,
                    playerState,
                    onPlaybackEvent,
                    Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
            }
        } else {
            Row(containerModifier.statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                Pager(
                    expanded,
                    lyrics,
                    syncing,
                    playing,
                    pagerState,
                    queue,
                    playerState.time,
                    onPlaybackEvent,
                    onUiEvent,
                    Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .aspectRatio(1f, true)
                ) { path, favorite ->
                    onPlayerEvent(PlayerEvent.UpdateFavorite(path, favorite))
                }
                VerticalDivider(Modifier.fillMaxHeight())
                Controls(
                    activeItem,
                    uiState.showVolumeSlider,
                    playerState,
                    onPlaybackEvent,
                    Modifier.weight(1f),
                )
            }
        }
        val playlistOffset =
            if (playlistState.offset.isNaN()) {
                height.toFloat()
            } else {
                playlistState.offset
            }
        Queue(
            queue,
            playerState = playerState,
            lyrics = lyrics,
            playlistHidden = playlistState.settledValue == PlaylistViewState.COLLAPSED,
            onPlaybackEvent = onPlaybackEvent,
            onUiEvent = onUiEvent,
            progress = playlistProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(height.toDp() + 72.dp)
                .offset {
                    IntOffset(
                        0,
                        playlistOffset.roundToInt(),
                    )
                }
                .anchoredDraggable(
                    playlistState,
                    Orientation.Vertical,
                    playlistDragEnabled,
                    flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                        playlistState,
                        { it * .5f },
                        tween(),
            //                        splineBasedDecay(density),
            //                        { with(density) { 100.dp.toPx() } }
                    )
                ),
            onCollapse = {
                scope.launch {
                    playlistState.animateTo(PlaylistViewState.COLLAPSED)
                }
            }
        ) {
            scope.launch {
                playlistState.animateTo(PlaylistViewState.EXPANDED)
            }
        }
    }
}

fun getIndex(
    list: List<Long>,
    time: Long,
): Int =
    when {
        list.isEmpty() -> -1
        (list.count() == 1) or (list.first() >= time) -> 0
        (list[1] > time) and (list[0] <= time) -> 0
        else -> 1 + getIndex(list - list.first(), time)
    }
