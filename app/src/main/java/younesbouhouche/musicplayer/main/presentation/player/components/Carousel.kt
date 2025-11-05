package younesbouhouche.musicplayer.main.presentation.player.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.models.QueueModel
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import kotlin.math.absoluteValue

@Composable
fun Carousel(
    queue: QueueModel,
    modifier: Modifier = Modifier,
    animate: Boolean = false,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    val pagerState = rememberPagerState {
        queue.items.size
    }
    val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            if ((pagerState.currentPage != pagerState.settledPage) and pagerIsDragged) {
                onPlaybackEvent(PlaybackEvent.Seek(it))
            }
        }
    }
    LaunchedEffect(queue) {
        pagerState.animateScrollToPage(queue.index)
    }
    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .aspectRatio(1f, true),
        contentPadding = PaddingValues(horizontal = 16.dp),
        snapPosition = SnapPosition.Center,
        key = { queue.items[it].id },
    ) { page ->
        val transition = rememberInfiniteTransition(label = "Playing animation")
        val animatedScale by transition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(5000),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "Scale animation",
        )
        val diskScale by animateFloatAsState(
            targetValue =
                if (animate and (queue.index == page))
                    animatedScale
                else 1f,
            label = "",
        )
        val pageOffset =
            (
                    (pagerState.currentPage - page) +
                            pagerState
                                .currentPageOffsetFraction
                    ).absoluteValue
        with(queue.items[page]) {
            MyImage(
                model = coverUri,
                icon = Icons.Default.MusicNote,
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
                shape = MaterialTheme.shapes.extraLarge,
                background = MaterialTheme.colorScheme.surface.copy(
                    if (pageOffset < 0.5f) 1 - pageOffset else 0.5f
                ),
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f, true)
                    .scale(lerp(0.8f, 1f, 1 - pageOffset))
                    .scale(diskScale),
            )
        }
    }
}