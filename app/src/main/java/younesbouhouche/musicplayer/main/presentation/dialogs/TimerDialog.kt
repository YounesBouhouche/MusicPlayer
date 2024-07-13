package younesbouhouche.musicplayer.main.presentation.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.Dialog
import younesbouhouche.musicplayer.core.presentation.RadioContainer
import younesbouhouche.musicplayer.main.domain.events.TimerType
import younesbouhouche.musicplayer.main.presentation.util.timeString
import kotlin.math.roundToLong

@Composable
fun TimerDialog(
    visible: Boolean,
    initialTimerType: TimerType = TimerType.Disabled,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (TimerType) -> Unit,
) {
    var timePickerDialog by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf(initialTimerType) }
    LaunchedEffect(key1 = visible) {
        if (visible) type = initialTimerType
    }
    Dialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.timer),
        cancelListener = onDismissRequest,
        okListener = {
            onConfirmRequest(type)
            onDismissRequest()
        },
    ) {
        RadioContainer(
            selected = type == TimerType.Disabled,
            onSelected = { type = TimerType.Disabled },
            text = stringResource(R.string.disabled),
        )
        RadioContainer(
            selected = type is TimerType.Duration,
            onSelected = { type = TimerType.Duration(60000L) },
            text = "Duration",
        ) {
            if (type is TimerType.Duration) {
                val time = (((type as TimerType.Duration).ms) / 60000).toInt()
                Text(pluralStringResource(R.plurals.minutes, time, time))
                Spacer(Modifier.width(8.dp))
            }
        }
        if (type is TimerType.Duration) {
            Spacer(Modifier.height(8.dp))
            Slider(
                value = (type as TimerType.Duration).ms / 60000f,
                onValueChange = { value ->
                    type = TimerType.Duration((value * 60000f).roundToLong())
                },
                valueRange = 1f..60f,
                steps = 59,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .padding(horizontal = 24.dp),
                colors =
                    SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
            )
            Spacer(Modifier.height(16.dp))
        }
        RadioContainer(
            selected = type is TimerType.Time,
            onSelected = { type = TimerType.Time(0, 0) },
            text = stringResource(R.string.time),
        ) {
            if (type is TimerType.Time) {
                with((type as TimerType.Time)) {
                    Text(((hour * 60 + min) * 60000L).timeString.dropLast(3))
                }
            }
            IconButton(
                enabled = type is TimerType.Time,
                onClick = { timePickerDialog = true },
            ) {
                Icon(Icons.Default.Edit, null)
            }
        }
        RadioContainer(
            selected = type is TimerType.End,
            onSelected = { type = TimerType.End(1) },
            text = stringResource(R.string.end_of_tracks),
        ) {
            var selectedTracks by remember { mutableStateOf("1") }
            LaunchedEffect(key1 = selectedTracks) {
                type = TimerType.End((selectedTracks.toIntOrNull() ?: 1).coerceIn(1, null))
            }
            if (type is TimerType.End) {
                OutlinedTextField(
                    value = selectedTracks,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                    onValueChange = {
                        selectedTracks = it
                    },
                    modifier = Modifier.width(100.dp),
                )
            }
        }
    }
    TimePickerDialog(
        visible = timePickerDialog,
        initialTimer = if (type is TimerType.Time) type as TimerType.Time else TimerType.Time(0, 0),
        onDismissRequest = { timePickerDialog = false },
        onConfirmRequest = { h, m ->
            type = TimerType.Time(h, m)
            timePickerDialog = false
        },
    )
}
