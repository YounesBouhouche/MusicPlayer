package younesbouhouche.musicplayer.main.presentation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.models.QueueModel
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.states.ViewState
import younesbouhouche.musicplayer.ui.theme.AppTheme
import kotlin.math.roundToInt

@Composable
fun PlayerScreen(
    queue: QueueModel,
    playerState: PlayerState,
    offset: Int,
    viewHeight: Int,
    progress: Float,
    state: PlayerState,
    dragState: AnchoredDraggableState<ViewState>,
    modifier: Modifier = Modifier,
    onSetFavorite: (String, Boolean) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    val density = LocalDensity.current
    val height = with(density) {
        80.dp + ((viewHeight - 80.dp.toPx().roundToInt()) * progress).toDp()
    }
    val viewHeightDp = with(density) {
        viewHeight.toDp()
    }
    val largeCorner = 32.dp * (1f - progress)
    val smallCorner = 8.dp * (1f - progress)
    val palette = rememberPaletteState {  }
    val scope = rememberCoroutineScope()
    val shape = RoundedCornerShape(
        topStart = largeCorner,
        topEnd = largeCorner,
        bottomStart = smallCorner,
        bottomEnd = smallCorner
    )
    AppTheme(palette.palette) {
        Box(
            modifier.fillMaxWidth()
                .padding(16.dp * (1f - progress))
                .height(height)
                .offset {
                    IntOffset(0, offset)
                }
                .anchoredDraggable(
                    dragState,
                    Orientation.Vertical,
                    flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                        state = dragState,
                        animationSpec = tween(),
                        positionalThreshold = { it * .5f }
                    ),
                )
                .shadow(8.dp, shape)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            SmallPlayerScreen(
                queue.items.getOrNull(queue.index),
                state.playState,
                Modifier
                    .alpha(1f - progress)
                    .height(80.dp),
                progress < 1f,
                onPlaybackEvent,
                {
                    it?.asImageBitmap()?.let { image ->
                        scope.launch {
                            palette.generate(image)
                        }
                    } ?: palette.reset()
                }
            ) {
                scope.launch {
                    dragState.animateTo(ViewState.LARGE)
                }
            }
            if (progress > 0f) {
                LargePlayerScreen(
                    queue,
                    playerState,
                    Modifier
                        .alpha(progress)
                        .requiredHeight(viewHeightDp),
                    {
                        scope.launch {
                            dragState.animateTo(ViewState.SMALL)
                        }
                    },
                    onSetFavorite,
                    onPlaybackEvent
                )
            }
        }
    }
}