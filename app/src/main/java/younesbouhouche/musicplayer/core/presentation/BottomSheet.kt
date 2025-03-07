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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SkipNext
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.util.timeString
import java.io.File

data class BottomSheetButton(val text: String, val icon: ImageVector, val active: Boolean = false, val onClick: () -> Unit) {
    constructor(text: String, icon: ImageVector, onClick: () -> Unit) : this(text, icon, false, onClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    open: Boolean,
    state: SheetState,
    onDismissRequest: () -> Unit,
    leadingContent: (@Composable (LazyItemScope.() -> Unit))? = null,
    trailingContent: (@Composable (LazyItemScope.() -> Unit))? = null,
    buttons: List<List<BottomSheetButton>> = emptyList(),
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
                if (trailingContent != null) {
                    item(content = trailingContent)
                } else {
                    item {
                        Spacer(Modifier.height(navBarHeight))
                    }
                }
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
                        BottomSheetButton(stringResource(R.string.play_next), Icons.Outlined.SkipNext) {
                            onPlayerEvent(PlayerEvent.PlayNext(listOf(file)))
                        },
                        BottomSheetButton(stringResource(R.string.add_to_playing_queue), Icons.Default.AddToPhotos) {
                            onPlayerEvent(PlayerEvent.AddToQueue(listOf(file)))
                        },
                        BottomSheetButton(
                            stringResource(R.string.add_to_playlist),
                            Icons.AutoMirrored.Default.PlaylistAdd,
                        ) {
                            onUiEvent(UiEvent.ShowAddToPlaylistDialog(listOf(path)))
                        },
                    ),
                    listOf(
                        BottomSheetButton(stringResource(R.string.details), Icons.Outlined.Info) {
                            onUiEvent(UiEvent.ShowDetails(this))
                        },
                        BottomSheetButton(stringResource(R.string.share), Icons.Outlined.Share, shareFile),
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
                    Box(Modifier.size(80.dp).padding(8.dp)) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .clip(MaterialTheme.shapes.medium)
                                .clipToBounds()
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (cover == null) {
                                Icon(
                                    Icons.Default.MusicNote,
                                    null,
                                    Modifier.fillMaxSize(.75f),
                                    MaterialTheme.colorScheme.surface,
                                )
                            } else {
                                Image(
                                    cover!!.asImageBitmap(),
                                    null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                        Text(
                            duration.timeString,
                            Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = .8f),
                                    RoundedCornerShape(100),
                                )
                                .clip(RoundedCornerShape(100))
                                .shadow(8.dp, RoundedCornerShape(100))
                                .clipToBounds()
                                .padding(4.dp)
                                .fillMaxWidth(.8f),
                            MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
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
    cover: String? = null,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    delete: () -> Unit,
    addToHomeScreen: () -> Unit,
    savePlaylist: () -> Unit,
    shareFiles: () -> Unit,
) {
    val context = LocalContext.current
    BottomSheet(
        open = open,
        state = state,
        onDismissRequest = onDismissRequest,
        buttons =
            listOf(
                listOf(
                    BottomSheetButton(stringResource(R.string.play_next), Icons.Outlined.SkipNext) {
                        onPlayerEvent(PlayerEvent.PlayNext(files))
                    },
                    BottomSheetButton(stringResource(R.string.add_to_playing_queue), Icons.Default.AddToPhotos) {
                        onPlayerEvent(PlayerEvent.AddToQueue(files))
                    },
                    BottomSheetButton(
                        stringResource(R.string.add_to_playlist),
                        Icons.AutoMirrored.Default.PlaylistAdd,
                    ) {
                        onUiEvent(UiEvent.ShowAddToPlaylistDialog(files.map { it.path }))
                    },
                ),
                listOf(
                    BottomSheetButton(stringResource(R.string.rename_playlist), Icons.Outlined.Edit) {
                        onUiEvent(UiEvent.ShowRenamePlaylistDialog(id, title))
                    },
                    BottomSheetButton(stringResource(R.string.save_playlist), Icons.Default.Save, savePlaylist),
                    BottomSheetButton(stringResource(R.string.remove_playlist), Icons.Outlined.Delete, delete),
                    BottomSheetButton(stringResource(R.string.create_home_shortcut), Icons.Default.AppShortcut, addToHomeScreen),
                ),
                listOf(
                    BottomSheetButton(stringResource(R.string.share_files), Icons.Outlined.Share, shareFiles),
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
                        val file = File(context.filesDir, cover)
                        val request =
                            ImageRequest.Builder(context)
                                .data(file)
                                .build()
                        Image(
                            rememberAsyncImagePainter(request),
                            null,
                            Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
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
                        text = pluralStringResource(R.plurals.item_s, files.size, files.size),
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
                        Icons.Outlined.PlayArrow,
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
                    BottomSheetButton(stringResource(R.string.play_next), Icons.Outlined.SkipNext) {
                        onPlayerEvent(PlayerEvent.PlayNext(list))
                    },
                    BottomSheetButton(stringResource(R.string.add_to_playing_queue), Icons.Default.AddToPhotos) {
                        onPlayerEvent(PlayerEvent.AddToQueue(list))
                    },
                    BottomSheetButton(
                        stringResource(R.string.add_to_playlist),
                        Icons.AutoMirrored.Filled.PlaylistAdd,
                    ) {
                        onUiEvent(UiEvent.ShowAddToPlaylistDialog(list.map { it.path }))
                    },
                ),
                listOf(
                    BottomSheetButton(stringResource(R.string.share_files), Icons.Outlined.Share, shareFiles),
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
