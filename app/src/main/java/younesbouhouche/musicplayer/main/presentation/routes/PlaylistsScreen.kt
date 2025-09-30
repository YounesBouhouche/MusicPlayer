package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.getPictureRequest
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.main.presentation.components.EmptyContainer
import younesbouhouche.musicplayer.main.presentation.components.GridScreen
import younesbouhouche.musicplayer.main.presentation.components.ListItem
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import younesbouhouche.musicplayer.main.presentation.components.PictureCard
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortBottomSheet
import younesbouhouche.musicplayer.main.presentation.util.SortState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlaylistsScreen(
    playlists: List<Playlist>,
    smallPlayerExpanded: Boolean,
    sortState: SortState<ListsSortType>,
    onSortStateChange: (SortState<ListsSortType>) -> Unit,
    modifier: Modifier = Modifier,
    onCreatePlaylist: () -> Unit = {},
    onImportPlaylist: () -> Unit = {},
    onPlay: (Playlist) -> Unit = {},
    onClick: (Playlist) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val menu = listOf(
        Triple(R.string.create_playlist, Icons.Default.Add, onCreatePlaylist),
        Triple(R.string.import_playlist, Icons.Default.FileDownload, onImportPlaylist),
    )
    val bottomInset by animateDpAsState(
        if (smallPlayerExpanded) 200.dp else 120.dp
    )
    Scaffold(
        modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(bottom = bottomInset),
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded,
                {
                    ToggleFloatingActionButton(
                        expanded,
                        { expanded = !expanded },
                        containerSize = ToggleFloatingActionButtonDefaults.containerSizeMedium()
                    ) {
                        val imageVector by remember {
                            derivedStateOf {
                                if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                            }
                        }
                        Icon(
                            imageVector,
                            null,
                            Modifier.animateIcon(
                                { checkedProgress },
                                size = ToggleFloatingActionButtonDefaults.iconSizeMedium()
                            ),
                        )
                    }
                },
                modifier = Modifier.offset(x = (16).dp)
            ) {
                menu.forEach { (text, icon, action) ->
                    FloatingActionButtonMenuItem(
                        text = { Text(stringResource(text)) },
                        onClick = {
                            expanded = false
                            action()
                        },
                        icon = {
                            Icon(icon, null)
                        },
                    )
                }
            }
        }
    ) { paddingValues ->
        EmptyContainer(
            playlists.isEmpty(),
            Icons.AutoMirrored.Filled.PlaylistPlay,
            stringResource(R.string.empty_playlists_text),
        ) {
            GridScreen(
                playlists,
                sortState,
                {
                    onSortStateChange(sortState.copy(expanded = true))
                },
                { playlist ->
                    ListItem(
                        { onClick(playlist) },
                        leadingContent = {
                            MyImage(
                                playlist.getPictureRequest(),
                                Icons.AutoMirrored.Filled.PlaylistPlay,
                                Modifier.size(68.dp),
                            )
                        },
                        modifier = Modifier.animateItem(),
                        trailingContent = {
                            ExpressiveIconButton(
                                Icons.Default.PlayArrow,
                                size = IconButtonDefaults.mediumIconSize,
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            ) {
                                onPlay(playlist)
                            }
                        }
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                        ) {
                            Text(
                                playlist.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                pluralStringResource(
                                    R.plurals.item_s,
                                    playlist.items.size,
                                    playlist.items.size
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                { playlist ->
                    PictureCard(
                        playlist.getPictureRequest(),
                        Icons.AutoMirrored.Filled.PlaylistPlay,
                        {
                            onClick(playlist)
                        },
                        Modifier.animateItem()
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                        ) {
                            Text(
                                playlist.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                Modifier
                    .fillMaxSize()
                    .padding(),
                { it.id },
                paddingValues
            )
        }
    }
    SortBottomSheet(
        sortState,
        options = ListsSortType.entries,
        icon = {
            it.icon
        },
        text = {
            it.label
        },
        onSortStateChange = onSortStateChange,
    )
}