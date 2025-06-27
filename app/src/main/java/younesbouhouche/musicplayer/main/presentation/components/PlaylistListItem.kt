package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import coil.request.ImageRequest
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Playlist
import java.io.File

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistListItem(
    playlist: Playlist,
    animatedContentScope: AnimatedContentScope,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    background: Color = Color.Transparent,
) {
    val context = LocalContext.current
    val file = playlist.image?.let { File(context.filesDir, it) }
    MyListItem(
        onClick = onClick,
        onLongClick = onLongClick,
        headline = playlist.name,
        supporting = pluralStringResource(R.plurals.item_s, playlist.items.size, playlist.items.size),
        shape = shape,
        cover = {
            MyImage(
                model = file,
                icon = Icons.AutoMirrored.Filled.PlaylistPlay,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "playlist-${playlist.id}"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .fillMaxSize()
            )
        },
        modifier = modifier,
        background = background,
        trailingContent = {
            IconButton(onClick = onPlay) {
                Icon(Icons.Outlined.PlayArrow, null)
            }
            IconButton(onClick = onLongClick) {
                Icon(Icons.Default.MoreVert, null)
            }
        },
    )
}



@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    background: Color = Color.Transparent,
) {
    val context = LocalContext.current
    val file = playlist.image?.let { File(context.filesDir, it) }
    MyListItem(
        onClick = onClick,
        onLongClick = onLongClick,
        headline = playlist.name,
        supporting = pluralStringResource(R.plurals.item_s, playlist.items.size, playlist.items.size),
        shape = shape,
        cover = {
            MyImage(
                model = file,
                icon = Icons.AutoMirrored.Filled.PlaylistPlay,
                modifier = Modifier.fillMaxSize()
            )
        },
        modifier = modifier,
        background = background,
        trailingContent = {
            IconButton(onClick = onPlay) {
                Icon(Icons.Outlined.PlayArrow, null)
            }
            IconButton(onClick = onLongClick) {
                Icon(Icons.Default.MoreVert, null)
            }
        },
    )
}
