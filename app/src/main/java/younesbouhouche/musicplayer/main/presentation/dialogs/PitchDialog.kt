package younesbouhouche.musicplayer.main.presentation.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.Dialog
import younesbouhouche.musicplayer.main.presentation.util.round
import younesbouhouche.musicplayer.main.presentation.util.scale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun PitchDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    pitch: Float,
    onPitchChange: (Float) -> Unit,
) {
    var selectedPitch by remember { mutableFloatStateOf(pitch) }
    LaunchedEffect(visible) {
        selectedPitch = pitch
    }
    Dialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.pitch),
        trailingContent = {
            IconButton(onClick = { onPitchChange(1f) }) {
                Icon(Icons.Default.Refresh, null)
            }
        },
        cancelListener = onDismissRequest,
        okListener = {
            onPitchChange(selectedPitch)
            onDismissRequest()
        },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedIconButton(onClick = { selectedPitch = max(.25f, (selectedPitch - 0.05f).scale(2)) }) {
                Icon(Icons.Default.Remove, null, Modifier.size(ButtonDefaults.IconSize))
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { selectedPitch = 1f },
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "× ",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                AnimatedContent(
                    targetState = selectedPitch,
                    label = "",
                    transitionSpec = {
                        (
                                if (targetState > initialState) {
                                    slideInVertically { height -> height } + fadeIn() togetherWith
                                            slideOutVertically { height -> -height } + fadeOut()
                                } else {
                                    slideInVertically { height -> -height } + fadeIn() togetherWith
                                            slideOutVertically { height -> height } + fadeOut()
                                }
                                ).using(
                                SizeTransform(clip = false),
                            )
                    },
                ) {
                    Text(
                        text = it.round(2),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            OutlinedIconButton(onClick = { selectedPitch = min(2f, (selectedPitch + 0.05f).scale(2)) }) {
                Icon(Icons.Default.Add, null, Modifier.size(ButtonDefaults.IconSize))
            }
        }
        Spacer(Modifier.height(24.dp))
        Slider(
            value = selectedPitch,
            valueRange = 0.25f..2f,
            onValueChange = { value ->
                // round value to 0.05
                selectedPitch = (value * 20).roundToInt() / 20f
            },
            // set steps to 0.05 in range 0.25 to 2 to avoid floating point errors
            steps = 35,
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors =
            SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        )
    }
}
