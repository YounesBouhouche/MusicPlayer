package younesbouhouche.musicplayer.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.ui.components.Dialog


@Composable
fun NewPlaylistDialog(
    visible: Boolean,
    playlistName: String,
    onNameChange: (String) -> Unit,
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
        OutlinedTextField(
            value = playlistName,
            onValueChange = onNameChange,
            label = { Text("Playlist name") },
            leadingIcon = {
                Icon(Icons.Default.Title, null)
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = playlistName.isNotEmpty(),
                    enter = materialSharedAxisZIn(true),
                    exit = materialSharedAxisZOut(true)
                ) {
                    IconButton(onClick = { onNameChange("") }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}