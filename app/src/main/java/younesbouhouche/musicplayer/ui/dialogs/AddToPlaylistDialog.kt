package younesbouhouche.musicplayer.ui.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.Playlist
import younesbouhouche.musicplayer.settings.settingsRadioItems
import younesbouhouche.musicplayer.ui.components.Dialog


@Composable
fun AddToPlaylistDialog(
    visible: Boolean,
    playlists: List<Playlist>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    Dialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = "Create Playlist",
        cancelListener = onDismissRequest,
        okListener = {
            onConfirmRequest()
            onDismissRequest()
        }
    ) {
        LazyColumn(Modifier.fillMaxWidth()) {
            settingsRadioItems(
                playlists,
                selectedIndex,
                onIndexChange
            ) {
                Text(it.name)
            }
        }
    }
}