package younesbouhouche.musicplayer.core.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueBottomSheet(
    open: Boolean,
    state: SheetState,
    onDismissRequest: () -> Unit,
    clear: () -> Unit,
    save: () -> Unit,
    add: () -> Unit,
) {
    BottomSheet(
        open = open,
        state = state,
        onDismissRequest = onDismissRequest,
        buttons =
            listOf(
                listOf(
                    BottomSheetButton("Clear queue", Icons.Default.ClearAll, clear),
                    BottomSheetButton("Save queue", Icons.Default.Save, save),
                    BottomSheetButton("Add to playlist", Icons.AutoMirrored.Default.PlaylistAdd, add),
                ),
            ),
    )
}
