package younesbouhouche.musicplayer.main.presentation.components

import android.content.ClipData
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.AddToQueue
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveButton
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.main.presentation.util.containerClip
import younesbouhouche.musicplayer.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.main.presentation.util.timeString

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MusicCardBottomSheet(
    file: MusicCard?,
    onDismissRequest: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onPlay: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    file?.let {
        val data = listOf(
            (R.string.artist to Icons.Default.Person) to file.artist,
            (R.string.album to Icons.Default.Album) to file.album,
            (R.string.duration to Icons.Default.Timer) to file.duration.timeString,
            (R.string.genre to Icons.Default.Category) to (file.genre),
            (R.string.file_path to Icons.AutoMirrored.Filled.InsertDriveFile) to file.path,
        )
        ModalBottomSheet(
            onDismissRequest,
            modifier = modifier,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            LazyColumn(
                Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Row(Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MyImage(
                            file.coverUri,
                            Icons.Default.MusicNote,
                            Modifier.size(120.dp),
                            shape = MaterialShapes.Cookie12Sided.toShape()
                        )
                        Text(
                            file.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(2) { index ->
                            val (res, icon) =
                                if (index == 0) R.string.play to Icons.Default.PlayArrow
                                else R.string.add_to_playlist to Icons.Default.AddToQueue
                            val containerColor =
                                if (index == 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.tertiary
                            val onClick =
                                if (index == 0) onPlay
                                else onAddToQueue
                            val interactionSource = remember { MutableInteractionSource() }
                            val pressed by interactionSource.collectIsPressedAsState()
                            val weight by animateFloatAsState(if (pressed) 1.4f else 1f)
                            ExpressiveButton(
                                onClick = {
                                    onClick()
                                    onDismissRequest()
                                },
                                icon = icon,
                                text = stringResource(res),
                                modifier = Modifier.weight(weight).fillMaxWidth(),
                                size = ButtonDefaults.MediumContainerHeight,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = containerColor,
                                    contentColor = contentColorFor(containerColor)
                                ),
                                interactionSource = interactionSource
                            )
                        }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(3) { index ->
                            val icon = when (index) {
                                0 -> if (file.favorite) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder
                                1 -> Icons.AutoMirrored.Filled.PlaylistAdd
                                else -> Icons.Default.Share
                            }
                            val color = when (index) {
                                0 -> MaterialTheme.colorScheme.secondaryContainer
                                1 -> MaterialTheme.colorScheme.surfaceContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            val interactionSource = remember { MutableInteractionSource() }
                            val pressed by interactionSource.collectIsPressedAsState()
                            val weight by animateFloatAsState(if (pressed) 1.4f else 1f)
                            ExpressiveIconButton(
                                onClick = {
                                    when(index) {
                                        0 -> onToggleFavorite()
                                        1 -> onAddToPlaylist()
                                        2 -> onShare()
                                    }
                                },
                                icon = icon,
                                modifier = Modifier.weight(weight).height(80.dp),
                                size = IconButtonDefaults.largeIconSize,
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = color,
                                    contentColor = contentColorFor(color)
                                ),
                                interactionSource = interactionSource
                            )
                        }
                    }
                }
                item {
                    Column(Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        data.forEachIndexed { index, (iconPair, value) ->
                            val (res, icon) = iconPair
                            val text = stringResource(res)
                            Row(
                                Modifier
                                    .containerClip(
                                        MaterialTheme.colorScheme.surfaceContainerLowest,
                                        expressiveRectShape(index, data.size)
                                    )
                                    .clickable {
                                        scope.launch {
                                            clipboard.setClipEntry(
                                                ClipEntry(
                                                    ClipData.newPlainText(text, value)
                                                )
                                            )
                                        }
                                    }
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(icon, null, Modifier.size(30.dp))
                                Column(
                                    Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        value.ifEmpty { stringResource(R.string.unknown) },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}