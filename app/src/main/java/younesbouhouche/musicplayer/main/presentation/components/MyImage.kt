package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.glance.GlanceModifier
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage

@Composable
fun MyImage(
    model: Any?,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = "",
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        contentScale = ContentScale.Crop,
        loading = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.fillMaxSize(.5f))
            }
        },
        error = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(.6f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        onSuccess = onSuccess
    )
}