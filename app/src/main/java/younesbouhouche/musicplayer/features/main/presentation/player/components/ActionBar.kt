package younesbouhouche.musicplayer.features.main.presentation.player.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import younesbouhouche.musicplayer.features.main.domain.events.TimerType
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEvent

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActionBar(
    shuffle: Boolean,
    repeatMode: Int,
    speed: Float,
    pitch: Float,
    timer: TimerType,
    showTimerSheet: () -> Unit,
    showSpeedSheet: () -> Unit,
    onPlayerEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface.copy(.4f),
        shape = RoundedCornerShape(100),
    ) {
        FlowRow(
            Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            repeat(4) {
                val icon = when(it) {
                    0 ->
                        if (shuffle) Icons.Default.ShuffleOn
                        else Icons.Default.Shuffle
                    1 -> when(repeatMode) {
                        Player.REPEAT_MODE_ALL -> Icons.Default.RepeatOn
                        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOneOn
                        else -> Icons.Default.Repeat
                    }
                    2 -> Icons.Default.Timer
                    else -> Icons.Default.Speed
                }
                val active = when(it) {
                    0 -> shuffle
                    1 -> repeatMode != Player.REPEAT_MODE_OFF
                    2 -> timer != TimerType.Disabled
                    else -> (speed != 1f) or (pitch != 1f)
                }
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val weight by animateFloatAsState(
                    if (pressed) 1.4f else 1f
                )
                ToggleButton(
                    checked = active,
                    onCheckedChange = { _ ->
                        when(it) {
                            0 -> onPlayerEvent(PlayerEvent.ToggleShuffle)
                            1 -> onPlayerEvent(PlayerEvent.CycleRepeatMode)
                            2 -> showTimerSheet()
                            else -> showSpeedSheet()
                        }
                    },
                    shapes =
                        if (it == 0)
                            ButtonGroupDefaults.connectedLeadingButtonShapes()
                        else if (it < 3)
                            ButtonGroupDefaults.connectedMiddleButtonShapes()
                        else ButtonGroupDefaults.connectedTrailingButtonShapes(),
                    modifier = Modifier
                        .heightIn(ButtonDefaults.MediumContainerHeight)
                        .semantics { role = Role.Checkbox }
                        .weight(weight),
                    contentPadding = ButtonDefaults
                        .contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                    colors = ToggleButtonDefaults.toggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(.1f)
                    ),
                    interactionSource = interactionSource,
                ) {
                    Icon(icon, null)
                }
            }
        }
    }
}