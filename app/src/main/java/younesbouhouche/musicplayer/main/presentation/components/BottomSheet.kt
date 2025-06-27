package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kmpalette.rememberPaletteState
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.ui.theme.AppTheme
import java.io.File

data class BottomSheetButton(
    val text: Int,
    val icon: ImageVector,
    val active: Boolean = false,
    val onClick: () -> Unit
)

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
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = WindowInsets.navigationBars.asPaddingValues()
            ) {
                leadingContent?.run {
                    item(content = this)
                }
                buttons.forEachIndexed { index, group ->
                    itemsIndexed(group) { index, item ->
                        SettingsItem(
                            headline = stringResource(item.text),
                            leadingContent = {
                                Icon(item.icon, null)
                            },
                            shape = listItemShape(index, group.size),
                            onClick = {
                                item.onClick()
                                onDismissRequest()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                        )
                    }
                    if (index < group.size - 1)
                        item {
                            Spacer(Modifier.height(6.dp))
                        }
                }
                trailingContent?.let {
                    item(content = it)
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
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onUpdateFavorite: (String, Boolean) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    navigateToAlbum: () -> Unit,
    navigateToArtist: () -> Unit,
    shareFile: () -> Unit,
) {
    val paletteState = rememberPaletteState()
    AppTheme(paletteState.palette) {
        with(file) {
            BottomSheet(
                open = open,
                state = state,
                onDismissRequest = onDismissRequest,
                buttons =
                    listOf(
                        listOf(
                            BottomSheetButton(R.string.play_next, Icons.Outlined.SkipNext) {
                                onPlaybackEvent(PlaybackEvent.PlayNext(listOf(file)))
                            },
                            BottomSheetButton(
                                R.string.add_to_playing_queue,
                                Icons.Default.AddToPhotos
                            ) {
                                onPlaybackEvent(PlaybackEvent.AddToQueue(listOf(file)))
                            }
                        ),
                        listOf(
                            BottomSheetButton(R.string.details, Icons.Outlined.Info) {
                                onUiEvent(UiEvent.ShowDetails(this))
                            },
                            BottomSheetButton(
                                R.string.share,
                                Icons.Outlined.Share,
                                onClick = shareFile
                            ),
                        ),
                    ),
                leadingContent = {
                    Box(Modifier
                        .fillMaxWidth()
                        .height(140.dp)) {
                        Box(Modifier.fillMaxSize()) {
                            MyImage(
                                coverUri,
                                null,
                                Modifier.fillMaxSize(),
                                shape = RectangleShape
                            )
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush
                                            .verticalGradient(
                                                0f to BottomSheetDefaults.ContainerColor.copy(alpha = 0.5f),
                                                1f to BottomSheetDefaults.ContainerColor,
                                            )
                                    )
                            ) {

                            }
                        }
                        Row(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(
                                Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(
                                    4.dp,
                                    Alignment.CenterVertically
                                )
                            ) {
                                Text(
                                    text = title,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
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
                            IconButton(
                                {
                                    onPlaybackEvent(PlaybackEvent.Play(listOf(this@with)))
                                },
                                Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    null,
                                    Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            {
                                onUpdateFavorite(path, !favorite)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    if (favorite)
                                        MaterialTheme.colorScheme.errorContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceContainer,
                                contentColor =
                                    if (favorite)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(100),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                if (favorite) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,
                                null,
                                Modifier.size(30.dp)
                            )
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.favorite))
                        }
                        Button(
                            {
                                onUiEvent(UiEvent.ShowAddToPlaylistDialog(listOf(path)))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(100),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.PlaylistAdd,
                                null,
                                Modifier.size(30.dp)
                            )
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.add_to_playlist))
                        }
                    }
                },
            )
        }
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
    onPlaybackEvent: (PlaybackEvent) -> Unit,
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
                    BottomSheetButton(R.string.play_next, Icons.Outlined.SkipNext) {
                        onPlaybackEvent(PlaybackEvent.PlayNext(files))
                    },
                    BottomSheetButton(
                        R.string.add_to_playing_queue,
                        Icons.Default.AddToPhotos
                    ) {
                        onPlaybackEvent(PlaybackEvent.AddToQueue(files))
                    },
                    BottomSheetButton(
                        R.string.add_to_playlist,
                        Icons.AutoMirrored.Default.PlaylistAdd,
                    ) {
                        onUiEvent(UiEvent.ShowAddToPlaylistDialog(files.map { it.path }))
                    },
                ),
                listOf(
                    BottomSheetButton(R.string.rename_playlist, Icons.Outlined.Edit) {
                        onUiEvent(UiEvent.ShowRenamePlaylistDialog(id, title))
                    },
                    BottomSheetButton(
                        R.string.save_playlist,
                        Icons.Default.Save,
                        onClick = savePlaylist
                    ),
                    BottomSheetButton(
                        R.string.remove_playlist,
                        Icons.Outlined.Delete,
                        onClick = delete
                    ),
                    BottomSheetButton(
                        R.string.create_home_shortcut,
                        Icons.Default.AppShortcut,
                        onClick = addToHomeScreen
                    ),
                ),
                listOf(
                    BottomSheetButton(
                        R.string.share_files,
                        Icons.Outlined.Share,
                        onClick = shareFiles
                    ),
                ),
            ),
        leadingContent = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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
                        text = pluralStringResource(
                            R.plurals.item_s,
                            files.size,
                            files.size
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                IconButton(
                    onClick = {
                        onPlaybackEvent(PlaybackEvent.Play(files))
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
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    title: String,
    text: String,
    cover: Any? = null,
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
                    BottomSheetButton(
                        R.string.play_next,
                        Icons.Outlined.SkipNext
                    ) {
                        onPlaybackEvent(PlaybackEvent.PlayNext(list))
                    },
                    BottomSheetButton(
                        R.string.add_to_playing_queue,
                        Icons.Default.AddToPhotos
                    ) {
                        onPlaybackEvent(PlaybackEvent.AddToQueue(list))
                    },
                    BottomSheetButton(
                        R.string.add_to_playlist,
                        Icons.AutoMirrored.Filled.PlaylistAdd,
                    ) {
                        onUiEvent(UiEvent.ShowAddToPlaylistDialog(list.map { it.path }))
                    },
                ),
                listOf(
                    BottomSheetButton(
                        R.string.share_files,
                        Icons.Outlined.Share,
                        onClick = shareFiles
                    ),
                ),
            ),
        leadingContent = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    Modifier
                        .size(80.dp)
                        .padding(8.dp)
                        .clip(if (isMusicCard) MaterialTheme.shapes.medium else CircleShape)
                        .clipToBounds(),
                    contentAlignment = Alignment.Center,
                ) {
                    SubcomposeAsyncImage(
                        model = cover,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = {
                            Icon(
                                alternative,
                                null,
                                modifier = Modifier.fillMaxSize(),
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                        }
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
                    onPlaybackEvent(PlaybackEvent.Play(list))
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
