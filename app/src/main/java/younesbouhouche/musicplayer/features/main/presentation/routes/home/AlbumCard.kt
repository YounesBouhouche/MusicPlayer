package younesbouhouche.musicplayer.features.main.presentation.routes.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import com.younesb.mydesignsystem.presentation.components.Image
import younesbouhouche.musicplayer.core.domain.models.Album

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlbumCard(
    album: Album,
    shape: Shape,
    modifier: Modifier = Modifier,
    opacity: Float = 1f,
    onPlay: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth().aspectRatio(1f),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Box(Modifier.fillMaxSize()) {
            Image(
                album.cover,
                Icons.Default.Album,
                Modifier.blur(radiusX = 4.dp, radiusY = 4.dp).fillMaxSize(),
                background = MaterialTheme.colorScheme.surfaceVariant
            )
            Column(
                modifier = Modifier
                    .alpha(opacity)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
                    .fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    album.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    overflow = TextOverflow.Ellipsis
                )
                ExpressiveIconButton(
                    icon = Icons.Default.PlayArrow,
                    onClick = onPlay,
                    modifier = Modifier.padding(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(),
                    size = IconButtonDefaults.mediumIconSize,
                    widthOption = IconButtonDefaults.IconButtonWidthOption.Wide
                )
            }
        }
    }
}