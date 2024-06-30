package younesbouhouche.musicplayer.ui.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.models.Playlist
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
        title = "Add to playlist",
        cancelListener = onDismissRequest,
        okListener = {
            onConfirmRequest()
            onDismissRequest()
        }
    ) {
        if (playlists.isEmpty()) {
            Spacer(Modifier.height(16.dp))
            Icon(
                Icons.AutoMirrored.Default.PlaylistPlay,
                null,
                Modifier.size(64.dp).align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
            Text("No playlists available", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
        }
        else
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