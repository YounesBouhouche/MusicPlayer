package younesbouhouche.musicplayer.main.presentation.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.core.presentation.util.composables.isCompact
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.util.timeString

@Composable
fun SmallPlayer(
    queue: List<MusicCard>,
    index: Int,
    playerState: PlayerState,
    onPlayerEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cutout = if (isCompact) Modifier else Modifier.displayCutoutPadding()
    val item = queue[index]
    Box(modifier.height(80.dp).fillMaxWidth().then(cutout)) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f, true)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    AnimatedContent(
                        targetState = index,
                        label = "",
                        transitionSpec = {
                            (
                                if (initialState < targetState) {
                                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                                } else {
                                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                                }
                            )
                                .using(SizeTransform(clip = false))
                        },
                    ) {
                        if (queue[it].cover == null) {
                            Box(
                                Modifier.fillMaxSize()
                                    .clip(MaterialTheme.shapes.medium).clipToBounds().background(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.shapes.medium,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Default.MusicNote,
                                    null,
                                    Modifier.fillMaxSize(.8f),
                                    MaterialTheme.colorScheme.surfaceContainer,
                                )
                            }
                        } else {
                            Image(
                                queue[it].cover!!.asImageBitmap(),
                                null,
                                Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium).clipToBounds(),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        item.title,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        "${playerState.time.timeString} / ${item.duration.timeString}",
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                IconButton(onClick = { onPlayerEvent(PlayerEvent.Previous) }) {
                    Icon(Icons.Default.SkipPrevious, null)
                }
                IconButton(onClick = { onPlayerEvent(PlayerEvent.PauseResume) }) {
                    Icon(
                        if (playerState.playState == PlayState.PAUSED) {
                            Icons.Default.PlayArrow
                        } else {
                            Icons.Default.Pause
                        },
                        null,
                    )
                }
                IconButton(onClick = { onPlayerEvent(PlayerEvent.Next) }) {
                    Icon(Icons.Default.SkipNext, null)
                }
            }
            LinearProgressIndicator(
                progress = { playerState.time.toFloat() / item.duration },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
