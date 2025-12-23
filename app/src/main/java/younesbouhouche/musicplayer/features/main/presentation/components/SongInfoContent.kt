package younesbouhouche.musicplayer.features.main.presentation.components

import android.content.ClipData
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.AddToQueue
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.younesb.mydesignsystem.presentation.components.ExpressiveButton
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import com.younesb.mydesignsystem.presentation.components.Image
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.presentation.util.containerClip
import younesbouhouche.musicplayer.features.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.features.main.presentation.util.shareFile
import younesbouhouche.musicplayer.features.main.presentation.util.timeString
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.SongInfoViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SongInfoContent(
    trackId: Long,
    onAddToPlaylist: () -> Unit = {},
    onEditMetadata: () -> Unit = {},
) {
    val viewModel: SongInfoViewModel = koinViewModel(
        parameters = { parametersOf(trackId) }
    )
    val context = LocalContext.current
    val song by viewModel.song.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    song?.let { song ->
        val data = listOf(
            listOf(
                Triple(R.string.artist, Icons.Default.Person, song.artist),
                Triple(R.string.album, Icons.Default.Album, song.album),
            ),
            listOf(Triple(R.string.duration, Icons.Default.Timer, song.duration.timeString)),
            listOf(
                Triple(R.string.genre, Icons.Default.Category, song.genre),
                Triple(R.string.composer, Icons.Default.Person, song.composer),
            ),
            listOf(Triple(R.string.file_path, Icons.AutoMirrored.Filled.InsertDriveFile, song.path)),
        )
        Column(
            Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    song.coverUri,
                    Icons.Default.MusicNote,
                    Modifier.size(120.dp),
                    shape = MaterialShapes.Cookie12Sided.toShape()
                )
                Text(
                    song.title,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium
                )
                ExpressiveIconButton(
                    Icons.Default.Edit,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(),
                    onClick = onEditMetadata,
                    size = IconButtonDefaults.largeIconSize,
                    widthOption = IconButtonDefaults.IconButtonWidthOption.Narrow
                )
            }
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(2) { index ->
                    val (res, icon) =
                        if (index == 0) R.string.play to Icons.Default.PlayArrow
                        else R.string.add_to_queue to Icons.Default.AddToQueue
                    val containerColor =
                        if (index == 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.tertiary
                    val onClick =
                        if (index == 0) viewModel::play
                        else viewModel::addToQueue
                    val interactionSource = remember { MutableInteractionSource() }
                    val pressed by interactionSource.collectIsPressedAsState()
                    val weight by animateFloatAsState(if (pressed) 1.4f else 1f)
                    ExpressiveButton(
                        onClick = onClick,
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
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(3) { index ->
                    val icon = when (index) {
                        0 -> if (song.isFavorite) Icons.Default.Favorite
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
                                0 -> viewModel.toggleFavorite()
                                1 -> onAddToPlaylist()
                                2 -> context.shareFile(
                                    File(song.path),
                                    "audio/*"
                                )
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
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                data.forEachIndexed { index, line ->
                    Row(
                        Modifier.fillMaxWidth().clip(
                            expressiveRectShape(index,data.size)
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        line.forEach { (res, icon, value) ->
                            val text = stringResource(res)
                            Row(
                                Modifier
                                    .weight(1f)
                                    .containerClip(
                                        MaterialTheme.colorScheme.surfaceContainerLowest,
                                        MaterialTheme.shapes.medium,
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
                                        value?.takeIf { it.isNotBlank() }
                                            ?: stringResource(R.string.unknown),
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