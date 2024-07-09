package younesbouhouche.musicplayer.main.presentation.dialogs

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.core.presentation.Dialog

@Composable
fun CreatePlaylistDialog(
    visible: Boolean,
    playlistName: String,
    onNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = visible) {
        if (visible) focusRequester.requestFocus()
    }
    Dialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = "Create Playlist",
        cancelListener = onDismissRequest,
        okListener = {
            onConfirmRequest()
            onDismissRequest()
        },
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
                    exit = materialSharedAxisZOut(true),
                ) {
                    IconButton(onClick = { onNameChange("") }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            },
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .focusRequester(focusRequester),
        )
    }
}
