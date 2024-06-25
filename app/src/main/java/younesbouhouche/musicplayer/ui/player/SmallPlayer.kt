package younesbouhouche.musicplayer.ui.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import younesbouhouche.musicplayer.MusicCard
import younesbouhouche.musicplayer.PlayerEvent
import younesbouhouche.musicplayer.states.PlayState
import younesbouhouche.musicplayer.states.PlayerState
import younesbouhouche.musicplayer.timeString

@Composable
fun SmallPlayer(
    item: MusicCard,
    playerState: PlayerState,
    onPlayerEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.height(80.dp).fillMaxWidth()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f, true)
                        .padding(10.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clipToBounds(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(targetState = item.cover, label = "") {
                        if (it == null)
                            Icon(
                                Icons.Default.MusicNote,
                                null,
                                Modifier.fillMaxSize(.8f),
                                MaterialTheme.colorScheme.primary
                            )
                        else
                            Image(
                                it.asImageBitmap(),
                                null,
                                Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        item.title,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "${playerState.time.timeString} / ${item.duration.timeString}",
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = { onPlayerEvent(PlayerEvent.Previous) }) {
                    Icon(Icons.Default.SkipPrevious, null)
                }
                IconButton(onClick = { onPlayerEvent(PlayerEvent.PauseResume) }) {
                    Icon(
                        if (playerState.playState == PlayState.PAUSED)
                            Icons.Default.PlayArrow
                        else
                            Icons.Default.Pause,
                        null
                    )
                }
                IconButton(onClick = { onPlayerEvent(PlayerEvent.Next) }) {
                    Icon(Icons.Default.SkipNext, null)
                }
            }
            LinearProgressIndicator(
                progress = { playerState.time.toFloat() / item.duration },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}