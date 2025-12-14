package younesbouhouche.musicplayer.features.main.presentation.player.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.Image
import younesbouhouche.musicplayer.core.domain.models.Queue
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Carousel(
    queue: Queue,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onPlayerEvent: (PlayerEvent) -> Unit,
) {
    val carouselState = rememberCarouselState(
        queue.currentIndex
            .coerceIn(0, (queue.songs.size - 1).coerceAtLeast(0))
    ) {
        queue.songs.size
    }
    LaunchedEffect(queue.currentIndex) {
        if (carouselState.currentItem != queue.currentIndex) {
            carouselState.animateScrollToItem(queue.currentIndex)
            println("Scrolled to ${queue.currentIndex} from internal")
        }
    }
//    LaunchedEffect(carouselState.currentItem) {
//        if (carouselState.currentItem != queue.currentIndex) {
//            onPlayerEvent(PlayerEvent.Seek(carouselState.currentItem))
//            println("Scrolled to ${carouselState.currentItem} from external")
//        }
//    }
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
                            onPlayerEvent(PlayerEvent.Seek(carouselState.currentItem))
                    }
                ) { _, _ -> }
            },
        contentPadding = PaddingValues(horizontal = 16.dp),
        itemSpacing = 8.dp,
        userScrollEnabled = enabled
    ) { page ->
        Image(
            model = queue.songs.getOrNull(page)?.coverPath,
            icon = Icons.Default.MusicNote,
            iconTint = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
            shape = rememberMaskShape(MaterialTheme.shapes.extraLarge),
            background = MaterialTheme.colorScheme.surfaceContainer.copy(0.5f),
            onClick = {
                if (page != queue.currentIndex) {
                    onPlayerEvent(PlayerEvent.Seek(page))
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
            Queue(
                currentIndex = 0,
                songs =
                    List(5) {
                        Song.Builder()
                            .id(it.toLong())
                            .title("Title $it")
                            .artist("Artist $it")
                            .build()
                    }
            )
        )
    }
    var triggered by remember {
        mutableIntStateOf(0)
    }
    Surface(color = MaterialTheme.colorScheme.primaryContainer) {
        Column {
            Carousel(
                queue = queue,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f, true),
                enabled = true,
                onPlayerEvent = {
                    if (it is PlayerEvent.Seek) {
                        queue = queue.copy(currentIndex = it.index)
                        triggered++
                    }
                },
            )
            Row {
                Button({
                    queue = queue.copy(currentIndex = (queue.currentIndex - 1).coerceAtLeast(0))
                }) {
                    Text("Prev")
                }
                Text("${queue.currentIndex}")
                Button({
                    queue = queue.copy(currentIndex = (queue.currentIndex + 1).coerceAtMost(queue.songs.size - 1))
                }) {
                    Text("Next")
                }
            }
            Text("$triggered")
        }
    }
}