package younesbouhouche.musicplayer.core.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.core.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.MusicCard

data class BottomSheetButton(val text: String, val icon: ImageVector, val active: Boolean = false, val onClick: () -> Unit) {
    constructor(text: String, icon: ImageVector, onClick: () -> Unit) : this(text, icon, false, onClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    open: Boolean,
    state: SheetState,
    onDismissRequest: () -> Unit,
    leadingContent: (@Composable (LazyItemScope.() -> Unit))?,
    trailingContent: (@Composable (LazyItemScope.() -> Unit))?,
    buttons: List<List<BottomSheetButton>>,
) {
    if (open) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = state,
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
            dragHandle = null,
        ) {
            LazyColumn(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { }
                leadingContent?.run {
                    item(content = this)
                    item { HorizontalDivider() }
                }
                items(buttons) { group ->
                    group.forEach { item ->
                        TextButton(
                            colors =
                                ButtonDefaults.outlinedButtonColors().copy(
                                    contentColor = MaterialTheme.colorScheme.onBackground,
                                ),
                            onClick = {
                                item.onClick()
                                onDismissRequest()
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    item.icon,
                                    null,
                                    Modifier.size(24.dp),
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(item.text)
                            }
                        }
                    }
                }
                if (trailingContent != null) item(content = trailingContent)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemBottomSheet(
    open: Boolean,
    state: SheetState,
    onDismissRequest: () -> Unit,
    file: MusicCard,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    navigateToAlbum: () -> Unit,
    navigateToArtist: () -> Unit,
    shareFile: () -> Unit,
) {
    val favorite by file.favorite.collectAsState()
    with(file) {
        BottomSheet(
            open = open,
            state = state,
            onDismissRequest = onDismissRequest,
            buttons =
                listOf(
                    listOf(
                        BottomSheetButton("Play next", Icons.Default.SkipNext) {
                            onPlayerEvent(PlayerEvent.AddToNext(listOf(file)))
                        },
                        BottomSheetButton("Add to playing queue", Icons.Default.AddToPhotos) {
                            onPlayerEvent(PlayerEvent.AddToQueue(listOf(file)))
                        },
                        BottomSheetButton(
                            "Add to playlist",
                            Icons.AutoMirrored.Default.PlaylistAdd,
                        ) {
                            onUiEvent(UiEvent.ShowAddToPlaylistDialog(listOf(path)))
                        },
                    ),
                    listOf(
                        BottomSheetButton("Details", Icons.Outlined.Info) {
                            onUiEvent(UiEvent.ShowDetails(this))
                        },
                        BottomSheetButton("Share", Icons.Default.Share, shareFile),
                    ),
                ),
            leadingContent = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        Modifier
                            .size(80.dp)
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.shapes.medium,
                            )
                            .clip(MaterialTheme.shapes.medium)
                            .clipToBounds(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (cover != null) {
                            Image(
                                bitmap = cover!!.asImageBitmap(),
                                null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Icon(
                                Icons.Default.MusicNote,
                                null,
                                modifier = Modifier.fillMaxSize(0.7f),
                                tint = BottomSheetDefaults.ContainerColor,
                            )
                        }
                    }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            SuggestionChip(
                                modifier = Modifier.weight(1f, false),
                                onClick = {
                                    navigateToArtist()
                                    onDismissRequest()
                                },
                                label = {
                                    Text(
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        text = artist,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.Person,
                                        null,
                                        Modifier.size(16.dp),
                                    )
                                },
                            )
                            SuggestionChip(
                                modifier = Modifier.weight(1f, false),
                                onClick = {
                                    navigateToAlbum()
                                    onDismissRequest()
                                },
                                label = {
                                    Text(
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        text = album,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.Album,
                                        null,
                                        Modifier.size(16.dp),
                                    )
                                },
                            )
                        }
                    }
                    FilledIconToggleButton(
                        checked = favorite,
                        onCheckedChange = {
                            onPlayerEvent(PlayerEvent.UpdateFavorite(path, !favorite))
                        },
                        colors =
                            IconButtonDefaults.filledIconToggleButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                                checkedContentColor = MaterialTheme.colorScheme.error,
                                containerColor = Color.Transparent,
                                checkedContainerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            if (favorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            null,
                            Modifier.size(28.dp),
                        )
                    }
                }
            },
            trailingContent = { Spacer(Modifier.height(navBarHeight)) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheet(
    open: Boolean,
    state: SheetState,
    onDismissRequest: () -> Unit,
    id: Int,
    title: String,
    files: List<MusicCard>,
    cover: Bitmap? = null,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    delete: () -> Unit,
    addToHomeScreen: () -> Unit,
    savePlaylist: () -> Unit,
    shareFiles: () -> Unit,
) {
    BottomSheet(
        open = open,
        state = state,
        onDismissRequest = onDismissRequest,
        buttons =
            listOf(
                listOf(
                    BottomSheetButton("Play next", Icons.Default.SkipNext) {
                        onPlayerEvent(PlayerEvent.AddToNext(files))
                    },
                    BottomSheetButton("Add to playing queue", Icons.Default.AddToPhotos) {
                        onPlayerEvent(PlayerEvent.AddToQueue(files))
                    },
                    BottomSheetButton(
                        "Add to playlist",
                        Icons.AutoMirrored.Default.PlaylistAdd,
                    ) {
                        onUiEvent(UiEvent.ShowAddToPlaylistDialog(files.map { it.path }))
                    },
                ),
                listOf(
                    BottomSheetButton("Rename Playlist", Icons.Default.Edit) {
                        onUiEvent(UiEvent.ShowRenamePlaylistDialog(id, title))
                    },
                    BottomSheetButton("Save Playlist", Icons.Default.Save, savePlaylist),
                    BottomSheetButton("Remove Playlist", Icons.Default.DeleteSweep, delete),
                    BottomSheetButton("Create home shortcut", Icons.Default.AppShortcut, addToHomeScreen),
                ),
                listOf(
                    BottomSheetButton("Share Files", Icons.Default.Share, shareFiles),
                ),
            ),
        leadingContent = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    Modifier
                        .size(80.dp)
                        .padding(8.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clipToBounds(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (cover != null) {
                        Image(
                            bitmap = cover.asImageBitmap(),
                            null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Icon(
                            Icons.AutoMirrored.Default.PlaylistPlay,
                            null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${files.size} item(s)",
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                IconButton(
                    onClick = {
                        onPlayerEvent(PlayerEvent.Play(files))
                    },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        null,
                        Modifier.size(28.dp),
                    )
                }
            }
        },
        trailingContent = { Spacer(Modifier.height(navBarHeight)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBottomSheet(
    open: Boolean,
    list: List<MusicCard>,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    title: String,
    text: String,
    cover: Bitmap? = null,
    alternative: ImageVector = Icons.AutoMirrored.Default.PlaylistPlay,
    state: SheetState,
    shareFiles: () -> Unit = {},
) {
    val isMusicCard = alternative == Icons.Default.MusicNote
    BottomSheet(
        open = open,
        state = state,
        onDismissRequest = { onUiEvent(UiEvent.HideListBottomSheet) },
        buttons =
            listOf(
                listOf(
                    BottomSheetButton("Play next", Icons.Default.SkipNext) {
                        onPlayerEvent(PlayerEvent.AddToNext(list))
                    },
                    BottomSheetButton("Add to playing queue", Icons.Default.AddToPhotos) {
                        onPlayerEvent(PlayerEvent.AddToQueue(list))
                    },
                    BottomSheetButton(
                        "Add to playlist",
                        Icons.AutoMirrored.Filled.PlaylistAdd,
                    ) {
                        onUiEvent(UiEvent.ShowAddToPlaylistDialog(list.map { it.path }))
                    },
                ),
                listOf(
                    BottomSheetButton("Share Files", Icons.Default.Share, shareFiles),
                ),
            ),
        leadingContent = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    Modifier
                        .size(80.dp)
                        .padding(8.dp)
                        .clip(
                            if (isMusicCard) {
                                MaterialTheme.shapes.medium
                            } else {
                                CircleShape
                            },
                        )
                        .clipToBounds(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (cover != null) {
                        Image(
                            bitmap = cover.asImageBitmap(),
                            null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Icon(
                            alternative,
                            null,
                            modifier = Modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = text,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = {
                    onPlayerEvent(PlayerEvent.Play(list))
                    onUiEvent(UiEvent.HideListBottomSheet)
                }) {
                    Icon(
                        Icons.Default.PlayArrow,
                        null,
                        Modifier.size(28.dp),
                    )
                }
            }
        },
        trailingContent = { Spacer(Modifier.height(navBarHeight)) },
    )
}
