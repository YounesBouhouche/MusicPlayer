package younesbouhouche.musicplayer.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.IntOffset
import younesbouhouche.musicplayer.models.MusicCard
import younesbouhouche.musicplayer.events.PlayerEvent
import younesbouhouche.musicplayer.events.UiEvent
import younesbouhouche.musicplayer.states.PlayerState
import younesbouhouche.musicplayer.states.PlaylistViewState
import younesbouhouche.musicplayer.states.UiState
import younesbouhouche.musicplayer.states.ViewState
import younesbouhouche.musicplayer.ui.player.LargePlayer
import younesbouhouche.musicplayer.ui.player.SmallPlayer
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerScreen(
    queue: List<MusicCard>,
    playerState: PlayerState,
    uiState: UiState,
    dragState: AnchoredDraggableState<ViewState>,
    playlistDragState: AnchoredDraggableState<PlaylistViewState>,
    progress: Float,
    playlistProgress: Float,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
) {
    val shape = MaterialTheme.shapes.large.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)
    val offset = if (dragState.offset.isNaN()) 0 else dragState.offset.roundToInt()
    Box(
        Modifier
            .fillMaxSize()
            .offset {
                IntOffset(0, offset)
            }
            .anchoredDraggable(dragState, Orientation.Vertical)
            .background(MaterialTheme.colorScheme.surfaceContainer, shape)
            .clip(shape)
            .clipToBounds()) {
        queue.getOrNull(playerState.index)?.run {
            SmallPlayer(
                this,
                playerState,
                onPlayerEvent,
                Modifier
                    .alpha(1f - progress)
                    .align(Alignment.TopStart)
                    .clickable { onUiEvent(UiEvent.SetViewState(ViewState.LARGE)) }
            )
            LargePlayer(
                queue,
                playerState,
                onPlayerEvent,
                uiState.lyricsVisible,
                onUiEvent,
                playlistDragState,
                playlistProgress,
                dragState.settledValue == ViewState.LARGE,
                Modifier.alpha(progress)
            )
        }
    }
}
