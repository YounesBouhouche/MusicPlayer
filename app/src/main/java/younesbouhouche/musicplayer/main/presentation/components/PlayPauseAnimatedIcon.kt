package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun PlayPauseAnimatedIcon(
    playing: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.play_to_pause_animation)
    Image(
        painter = rememberAnimatedVectorPainter(image, playing),
        contentDescription = null,
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint)
    )
}