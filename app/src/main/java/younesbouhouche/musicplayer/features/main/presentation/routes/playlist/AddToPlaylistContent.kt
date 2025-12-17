package younesbouhouche.musicplayer.features.main.presentation.routes.playlist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.younesb.mydesignsystem.presentation.components.ExpressiveButton
import com.younesb.mydesignsystem.presentation.components.Image
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.getPictureRequest
import younesbouhouche.musicplayer.features.main.presentation.components.EmptyContainer
import younesbouhouche.musicplayer.features.main.presentation.components.ListItem
import younesbouhouche.musicplayer.features.main.presentation.util.composables.TitleText
import younesbouhouche.musicplayer.features.main.presentation.util.expressiveRectShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddToPlaylistContent(
    ids: List<Long>,
    onCreatePlaylist: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val viewModel = koinViewModel<AddToPlaylistViewModel>()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val selected by viewModel.selected.collectAsStateWithLifecycle()
    val createPlaylistButton: @Composable () -> Unit = remember {
        @Composable {
            ExpressiveButton(
                stringResource(R.string.create_playlist),
                ButtonDefaults.MediumContainerHeight,
                onClick = onCreatePlaylist,
                icon = Icons.Default.Add,
                colors = ButtonDefaults.filledTonalButtonColors()
            )
        }
    }
    LaunchedEffect(Unit) {
        viewModel.onClearSelection()
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.navigationBarsPadding().padding(16.dp)
    ) {
        TitleText(stringResource(R.string.add_to_playlist))
        EmptyContainer(
            playlists.isEmpty(),
            icon = Icons.AutoMirrored.Filled.PlaylistPlay,
            text = stringResource(R.string.no_playlists),
            trailingContent = {
                createPlaylistButton()
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                playlists.forEachIndexed { index, playlist ->
                    val playlistSelected = playlist.id in selected
                    val imageShape = expressiveRectShape(
                        0,
                        1,
                        MaterialTheme.shapes.medium,
                        MaterialTheme.shapes.medium,
                        selected = playlistSelected
                    )
                    val background =
                        if (playlistSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceContainerHigh
                    val color =
                        if (playlistSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    ListItem(
                        background = background,
                        leadingContent = {
                            Image(
                                playlist.getPictureRequest(),
                                Icons.AutoMirrored.Filled.PlaylistPlay,
                                Modifier.size(60.dp),
                                shape = imageShape,
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
                                    viewModel.onToggleSelection(playlist.id)
                                }
                            )
                        },
                        onClick = {
                            viewModel.onToggleSelection(playlist.id)
                        },
                        shape = expressiveRectShape(
                            index,
                            playlists.size,
                            MaterialTheme.shapes.small,
                            MaterialTheme.shapes.large,
                            selected = playlistSelected
                        ),
                    ) {
                        Text(
                            playlist.name,
                            Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = color,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Row(Modifier.padding(vertical = 16.dp)) {
                    createPlaylistButton()
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
                    else R.string.ok to {
                        viewModel.addToPlaylists(ids)
                        onDismissRequest()
                    }
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
