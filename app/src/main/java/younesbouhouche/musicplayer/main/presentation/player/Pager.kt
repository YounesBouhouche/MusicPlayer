package younesbouhouche.musicplayer.main.presentation.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.QueueModel

@Composable
fun Pager(
    enabled: Boolean,
    lyricsVisible: Boolean,
    syncing: Boolean,
    playing: Boolean,
    pagerState: PagerState,
    queue: QueueModel,
    time: Long,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
    onUpdateFavorite: (String, Boolean) -> Unit,
) {
    AnimatedContent(
        targetState = lyricsVisible,
        label = "",
        modifier = modifier.padding(bottom = 8.dp),
        contentAlignment = Alignment.Center,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith
                    slideOutHorizontally { it } + fadeOut()
            }
        },
    ) { lyrics ->
        if (lyrics) {
            Lyrics(queue.items[queue.index].lyrics, syncing, time, onPlaybackEvent, onUiEvent)
        } else {
            Disk(enabled, queue, playing, pagerState, onUpdateFavorite)
        }
    }
}
