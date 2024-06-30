package younesbouhouche.musicplayer.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.events.TimerType
import younesbouhouche.musicplayer.ui.components.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    isCompact: Boolean = true,
    initialTimer: TimerType.Time,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (Int, Int) -> Unit
) {
    var input by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(initialTimer.hour, initialTimer.min)
    Dialog(
        isCompact = isCompact,
        visible = visible,
        title = "Pick Time",
        onDismissRequest = onDismissRequest,
        neutral = {
            IconButton(
                onClick = {
                    input = !input
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    if (input) Icons.Outlined.AccessTime else Icons.Default.Keyboard,
                    null
                )
            }
        },
        cancelListener = onDismissRequest,
        okListener = {
            onConfirmRequest(timePickerState.hour, timePickerState.minute)
        }
    ) {
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (input)
                TimeInput(
                    state = timePickerState,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                )
            else
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
        }
    }
}