package younesbouhouche.musicplayer.main.presentation.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.settings.presentation.util.Checked
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistDialog(
    visible: Boolean,
    playlists: List<Playlist>,
    selected: Set<Int>,
    onToggle: (Int) -> Unit,
    onCreatePlaylist: () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    val context = LocalContext.current
    if (visible)
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            LazyColumn(
                contentPadding =
                    PaddingValues(16.dp, 8.dp, 16.dp, 0.dp)
                            + WindowInsets.navigationBars.asPaddingValues(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(Modifier.padding(bottom = 4.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.add_to_playlist),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Button(onCreatePlaylist) {
                            Icon(
                                Icons.Default.Add,
                                null,
                                Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.create_new))
                        }
                    }
                }
                item {
                    AnimatedContent(playlists.isEmpty()) {
                        if (it)
                            Column(
                                Modifier.padding(vertical = 60.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.PlaylistPlay,
                                    null,
                                    Modifier.size(100.dp),
                                )
                                Text(
                                    stringResource(R.string.no_playlists),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        else
                            SettingsList(null) {
                                playlists.forEachIndexed { index, playlist ->
                                    SettingsItem(
                                        headline = playlist.name,
                                        useCheckbox = true,
                                        leadingContent = {
                                            val file = playlist.image?.let { File(context.filesDir, it) }
                                            Surface(
                                                modifier = Modifier.size(60.dp),
                                                shape = MaterialTheme.shapes.large
                                            ) {
                                                Box(Modifier.fillMaxSize()) {
                                                    MyImage(
                                                        file,
                                                        Icons.AutoMirrored.Filled.PlaylistPlay,
                                                        Modifier.fillMaxSize()
                                                    )
                                                }
                                            }
                                        },
                                        checked = Checked(false, index in selected) {
                                            onToggle(index)
                                        },
                                        onClick = {
                                            onToggle(index)
                                        },
                                        shape = listItemShape(index, playlists.size)
                                    )
                                }
                            }
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(onDismissRequest, Modifier.weight(1f)) {
                            Text(stringResource(R.string.cancel))
                        }
                        Button({
                            onConfirmRequest()
                            onDismissRequest()
                        }, Modifier.weight(1f)) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                }
            }
        }
}
