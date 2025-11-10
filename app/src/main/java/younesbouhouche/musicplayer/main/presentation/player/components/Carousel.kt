package younesbouhouche.musicplayer.main.presentation.player.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.models.QueueModel
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Carousel(
    queue: QueueModel,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    val carouselState = rememberCarouselState(
        queue.index
            .coerceIn(0, (queue.items.size - 1).coerceAtLeast(0))
    ) {
        queue.items.size
    }
    LaunchedEffect(queue.index) {
        if (carouselState.currentItem != queue.index)
            carouselState.animateScrollToItem(queue.index)
    }
    LaunchedEffect(carouselState.currentItem) {
        if (carouselState.currentItem != queue.index) {
            onPlaybackEvent(PlaybackEvent.Seek(carouselState.currentItem))
        }
    }
    HorizontalCenteredHeroCarousel(
        state = carouselState,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        println("Drag ended, current item: ${carouselState.currentItem}")
                        if (carouselState.currentItem != carouselState.currentItem)
                            onPlaybackEvent(PlaybackEvent.Seek(carouselState.currentItem))
                    }
                ) { _, _ -> }
            },
        contentPadding = PaddingValues(horizontal = 16.dp),
        itemSpacing = 8.dp,
        userScrollEnabled = enabled
    ) { page ->
        MyImage(
            model = queue.items.getOrNull(page)?.coverUri,
            icon = Icons.Default.MusicNote,
            iconTint = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
            shape = rememberMaskShape(MaterialTheme.shapes.extraLarge),
            background = MaterialTheme.colorScheme.surfaceContainer.copy(0.5f),
            onClick = {
                if (page != queue.index) {
                    onPlaybackEvent(PlaybackEvent.Seek(page))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .alpha(carouselItemDrawInfo.size / carouselItemDrawInfo.maxSize)
                .aspectRatio(1f, true),
        )
    }
}

@Preview
@Composable
private fun CarouselPreview() {
    var queue by remember {
        mutableStateOf(
            QueueModel(
                index = 0,
                items =
                    List(5) {
                        MusicCard.Builder()
                            .setId(it.toLong())
                            .setTitle("Title $it")
                            .setArtist("Artist $it")
                            .build()
                    }
            )
        )
    }
    var triggered by remember {
        mutableIntStateOf(0)
    }
    Surface(color = MaterialTheme.colorScheme.primaryContainer) {
        Column() {
            Carousel(
                queue = queue,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f, true),
                enabled = true,
                onPlaybackEvent = {
                    if (it is PlaybackEvent.Seek) {
                        queue = queue.copy(index = it.index)
                        triggered++
                    }
                },
            )
            Row() {
                Button({
                    queue = queue.copy(index = (queue.index - 1).coerceAtLeast(0))
                }) {
                    Text("Prev")
                }
                Text("${queue.index}")
                Button({
                    queue = queue.copy(index = (queue.index + 1).coerceAtMost(queue.items.size - 1))
                }) {
                    Text("Next")
                }
            }
            Text("$triggered")
        }
    }
}