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
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.MusicCard

@Composable
fun Pager(
    lyricsVisible: Boolean,
    syncing: Boolean,
    playing: Boolean,
    pagerState: PagerState,
    queue: List<MusicCard>,
    index: Int,
    time: Long,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = lyricsVisible, label = "",
        modifier = modifier.padding(bottom = 8.dp),
        contentAlignment = Alignment.Center,
        transitionSpec = {
            if (targetState > initialState)
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            else
                slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
        }) { lyrics ->
        if (lyrics)
            Lyrics(queue[index].lyrics, syncing, time, onPlayerEvent, onUiEvent)
        else
            Disk(queue, index, playing, pagerState, onPlayerEvent)
    }
}
