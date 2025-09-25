package younesbouhouche.musicplayer.main.presentation.dialogs

import android.view.Window
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.getPictureRequest
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveButton
import younesbouhouche.musicplayer.main.presentation.components.ListItem
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import younesbouhouche.musicplayer.main.presentation.util.composables.TitleText
import younesbouhouche.musicplayer.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.settings.presentation.util.Checked
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
    if (visible)
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            contentWindowInsets = {
                WindowInsets.navigationBars.add(WindowInsets(
                    16.dp,
                    16.dp,
                    16.dp,
                    16.dp
                ))
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleText(stringResource(R.string.add_to_playlist))
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
                    else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            playlists.forEachIndexed { index, playlist ->
                                val background =
                                    if (playlist.id in selected)
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = .5f)
                                    else
                                        MaterialTheme.colorScheme.surfaceContainer
                                val color =
                                    if (playlist.id in selected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                ListItem(
                                    background = background,
                                    leadingContent = {
                                        MyImage(
                                            playlist.getPictureRequest(),
                                            Icons.AutoMirrored.Filled.PlaylistPlay,
                                            Modifier.size(60.dp),
                                            iconTint = color,
                                            background = MaterialTheme.colorScheme.surface.copy(
                                                alpha = .5f
                                            )
                                        )
                                    },
                                    trailingContent = {
                                        Checkbox(
                                            playlist.id in selected,
                                            {
                                                onToggle(playlist.id)
                                            }
                                        )
                                    },
                                    onClick = {
                                        onToggle(playlist.id)
                                    },
                                    shape = expressiveRectShape(index, playlists.size),
                                ) {
                                    Text(
                                        playlist.name,
                                        Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = color
                                    )
                                }
                            }
                            ExpressiveButton(
                                text = stringResource(R.string.create_new),
                                size = ButtonDefaults.MediumContainerHeight,
                                icon = Icons.Default.Add,
                                onClick = onCreatePlaylist,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            )
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(2) {
                        val (res, onClick) =
                            if (it == 0) R.string.cancel to onDismissRequest
                            else R.string.ok to onConfirmRequest
                        val interactionSource = remember { MutableInteractionSource() }
                        val pressed by interactionSource.collectIsPressedAsState()
                        val weight by animateFloatAsState(
                            if (pressed) 1.4f else 1f
                        )
                        ExpressiveButton(
                            stringResource(res),
                            ButtonDefaults.MediumContainerHeight,
                            Modifier.weight(weight),
                            onClick = onClick,
                            outlined = it == 0,
                            interactionSource = interactionSource
                        )
                    }
                }
            }
        }
}
