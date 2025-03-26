package younesbouhouche.musicplayer.main.presentation.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.presentation.components.Dialog

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
    val photoPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = onImageChange,
        )
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
            Box(
                Modifier.size(200.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .clipToBounds()
                    .clickable {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.AddAPhoto,
                    null,
                    Modifier.size(60.dp),
                    MaterialTheme.colorScheme.onSurface,
                )
                image?.let {
                    Image(
                        painter = rememberAsyncImagePainter(image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    SmallFloatingActionButton({
                        onImageChange(null)
                    }, Modifier.align(Alignment.BottomEnd).offset((-16).dp, (-16).dp)) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
            }
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
