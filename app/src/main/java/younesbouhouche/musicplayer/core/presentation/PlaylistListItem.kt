package younesbouhouche.musicplayer.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.domain.models.Playlist
import java.io.File

@Composable
fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    MyListItem(
        onClick = onClick,
        onLongClick = onLongClick,
        headline = playlist.name,
        supporting = pluralStringResource(R.plurals.item_s, playlist.items.size, playlist.items.size),
        cover = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .clipToBounds(),
            ) {
                if (playlist.image == null) {
                    Icon(Icons.AutoMirrored.Filled.PlaylistPlay, null, Modifier.fillMaxSize())
                } else {
                    val file = File(context.filesDir, playlist.image)
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
                }
            }
        },
        modifier = modifier,
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
