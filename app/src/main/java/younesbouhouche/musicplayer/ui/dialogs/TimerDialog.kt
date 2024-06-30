package younesbouhouche.musicplayer.ui.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.events.TimerType
import younesbouhouche.musicplayer.timeString
import younesbouhouche.musicplayer.ui.components.Dialog
import younesbouhouche.musicplayer.ui.components.RadioContainer
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
        title = "Timer",
        cancelListener = onDismissRequest,
        okListener = {
            onConfirmRequest(type)
            onDismissRequest()
        }
    ) {
        RadioContainer(
            selected = type == TimerType.Disabled,
            onSelected = { type = TimerType.Disabled },
            text = "Disabled"
        )
        RadioContainer(
            selected = type is TimerType.Duration,
            onSelected = { type = TimerType.Duration(60000L) },
            text = "Duration"
        ) {
            if(type is TimerType.Duration) {
                Text("${((type as TimerType.Duration).ms) / 60000} minute(s)")
                Spacer(Modifier.width(8.dp))
            }
        }
        if(type is TimerType.Duration) {
            Spacer(Modifier.height(8.dp))
            Slider(
                value = (type as TimerType.Duration).ms / 60000f,
                onValueChange = { value ->
                    type = TimerType.Duration((value * 60000f).roundToLong())
                },
                valueRange = 1f..120f,
                steps = 119,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .padding(horizontal = 24.dp),
                colors = SliderDefaults.colors(
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
            text = "Time"
        ) {
            if(type is TimerType.Time) {
                with ((type as TimerType.Time)) {
                    Text(((hour * 60 + min) * 60000L).timeString.dropLast(3))
                }
            }
            IconButton(
                enabled = type is TimerType.Time,
                onClick = { timePickerDialog = true }) {
                Icon(Icons.Default.Edit, null)
            }
        }
        RadioContainer(
            selected = type is TimerType.End,
            onSelected = { type = TimerType.End(1) },
            text = "End of tracks"
        ) {
            if (type is TimerType.End)
                OutlinedTextField(
                    value = (type as TimerType.End).tracks.toString(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = {
                        type = TimerType.End(
                            try { it.toInt() } catch (_: Exception) { 1 }.coerceIn(1, null)
                        )
                    },
                    modifier = Modifier.size(80.dp, 46.dp)
                )
        }
    }
    TimePickerDialog(
        visible = timePickerDialog,
        initialTimer = if (type is TimerType.Time) type as TimerType.Time else TimerType.Time(0, 0),
        onDismissRequest = { timePickerDialog = false },
        onConfirmRequest = { h, m ->
            type = TimerType.Time(h, m)
            timePickerDialog = false
        }
    )
}