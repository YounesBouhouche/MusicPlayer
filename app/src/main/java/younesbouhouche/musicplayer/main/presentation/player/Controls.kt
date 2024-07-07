package younesbouhouche.musicplayer.main.presentation.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.MaterialSharedAxisZ
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.util.timeString
import kotlin.math.roundToLong

@Composable
fun Controls(
    activeItem: MusicCard,
    playerState: PlayerState,
    onPlayerEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRemaining by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val nextPrevColors =
        if (isSystemInDarkTheme())
            IconButtonDefaults.filledIconButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiaryContainer,
                containerColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        else
            IconButtonDefaults.filledIconButtonColors(
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
    Column(modifier.padding(horizontal = 30.dp)) {
        Text(
            text = activeItem.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = activeItem.artist,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(Modifier.height(24.dp))
        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            value =
            if ((dragging) or (playerState.loading)) sliderValue
            else (playerState.time.toFloat() / activeItem.duration),
            onValueChange = {
                sliderValue = it
                dragging = true
            },
            onValueChangeFinished = {
                dragging = false
                onPlayerEvent(PlayerEvent.SeekTime((sliderValue * activeItem.duration).roundToLong()))
            }
        )
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { },
                contentPadding = PaddingValues(4.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor =
                    animateColorAsState(
                        targetValue =
                        if (dragging) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(),
                        label = ""
                    ).value
                )
            ) {
                Text(
                    text = (
                            if (dragging) (activeItem.duration * sliderValue).roundToLong()
                            else playerState.time
                            ).timeString,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            MaterialSharedAxisZ(
                targetState = showRemaining,
                forward = true
            ) {
                TextButton(
                    onClick = { showRemaining = !showRemaining },
                    contentPadding = PaddingValues(4.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = (if (it) playerState.time - activeItem.duration else activeItem.duration).timeString,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { onPlayerEvent(PlayerEvent.Previous) },
                modifier = Modifier.size(80.dp),
                colors = nextPrevColors
            ) {
                Icon(
                    Icons.Outlined.SkipPrevious,
                    null,
                    modifier = Modifier.fillMaxSize(.5f)
                )
            }
            FilledIconButton(
                onClick = { onPlayerEvent(PlayerEvent.PauseResume) },
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .weight(1f),
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primaryContainer,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    if (playerState.playState == PlayState.PLAYING) Icons.Default.Pause
                    else Icons.Default.PlayArrow,
                    null,
                    modifier = Modifier.size(40.dp)
                )
            }
            FilledIconButton(
                onClick = { onPlayerEvent(PlayerEvent.Next) },
                modifier = Modifier.size(80.dp),
                colors = nextPrevColors
            ) {
                Icon(
                    Icons.Outlined.SkipNext,
                    null,
                    modifier = Modifier.fillMaxSize(.5f)
                )
            }
        }
    }
}