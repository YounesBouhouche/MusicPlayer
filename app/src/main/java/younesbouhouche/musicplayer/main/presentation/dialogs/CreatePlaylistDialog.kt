package younesbouhouche.musicplayer.main.presentation.dialogs

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.presentation.components.Dialog
import younesbouhouche.musicplayer.main.presentation.components.MyImagePicker

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreatePlaylistDialog(
    visible: Boolean,
    playlistName: String,
    onNameChange: (String) -> Unit,
    image: Uri? = null,
    onImageChange: (Uri?) -> Unit,
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
        title = stringResource(R.string.create_playlist),
        cancelListener = onDismissRequest,
        okListener = {
            onConfirmRequest()
            onDismissRequest()
        },
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MyImagePicker(
                image,
                modifier = Modifier.size(200.dp),
                shape = MaterialShapes.Cookie4Sided.toShape(),
                onPick = onImageChange,
                fraction = .3f
            )
            OutlinedTextField(
                value = playlistName,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.playlist_name)) },
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
}
