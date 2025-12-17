package younesbouhouche.musicplayer.features.main.presentation.routes.playlist

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.younesb.mydesignsystem.presentation.components.ButtonsRow
import com.younesb.mydesignsystem.presentation.components.ImagePicker
import com.younesb.mydesignsystem.presentation.components.TextField
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.presentation.components.DialogContent

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreatePlaylistContent(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    val viewModel = koinViewModel<CreatePlaylistViewModel>()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val uri by viewModel.uri.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.onNameChange("")
    }
    DialogContent(modifier) {
        ImagePicker(
            image = uri,
            onImageChange = viewModel::onUriChange,
            modifier = Modifier.size(200.dp),
            shape = MaterialShapes.Cookie4Sided.toShape(),
            fraction = .3f,
            background = MaterialTheme.colorScheme.tertiaryContainer,
            iconTint = MaterialTheme.colorScheme.onTertiaryContainer
        )
        TextField(
            value = name,
            onValueChange = viewModel::onNameChange,
            label = stringResource(R.string.playlist_name),
            modifier = Modifier.focusRequester(focusRequester)
        )
        ButtonsRow(
            count = 2,
            icon = { null },
            text = {
                stringResource(
                    if (it == 0) R.string.cancel
                    else R.string.create
                )
            },
            outlined = { it == 0 }
        ) {
            if (it == 0) {
                onDismissRequest()
            } else {
                viewModel.createPlaylist()
                onDismissRequest()
            }
        }
    }
}