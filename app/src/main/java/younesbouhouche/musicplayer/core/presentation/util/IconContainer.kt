package younesbouhouche.musicplayer.core.presentation.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import kotlin.let

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IconContainer(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconRatio: Float = 0.5f,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    shape: Shape = MaterialShapes.Cookie12Sided.toShape(),
    background: Color = MaterialTheme.colorScheme.surfaceContainer,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier
            .clip(shape)
            .background(background)
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            null,
            tint = iconTint,
            modifier = Modifier.fillMaxSize(iconRatio)
        )
    }
}