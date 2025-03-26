package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import younesbouhouche.musicplayer.R

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
                    BottomSheetButton(stringResource(R.string.clear_queue), Icons.Default.ClearAll, clear),
                    BottomSheetButton(stringResource(R.string.save_queue), Icons.Default.Save, save),
                    BottomSheetButton(stringResource(R.string.add_to_playlist), Icons.AutoMirrored.Default.PlaylistAdd, add),
                ),
            ),
    )
}
