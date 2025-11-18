package younesbouhouche.musicplayer.features.main.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackAnimation(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "playback_animation")

    // Create 4 different wave animations with different delays and speeds
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave2"
    )

    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave3"
    )

    val wave4 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave4"
    )

    Canvas(
        modifier = modifier.size(24.dp)
    ) {
        drawWaves(
            wave1 = wave1,
            wave2 = wave2,
            wave3 = wave3,
            wave4 = wave4,
            color = tint
        )
    }
}

private fun DrawScope.drawWaves(
    wave1: Float,
    wave2: Float,
    wave3: Float,
    wave4: Float,
    color: Color
) {
    val waveWidth = size.width / 7f
    val spacing = waveWidth * 0.5f
    val maxHeight = size.height

    // Draw 4 vertical wave bars
    val waves = listOf(wave1, wave2, wave3, wave4)

    waves.forEachIndexed { index, waveHeight ->
        val x = spacing + index * (waveWidth + spacing)
        val barHeight = maxHeight * waveHeight
        val y = (maxHeight - barHeight) / 2f

        drawRoundRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(x, y),
            size = androidx.compose.ui.geometry.Size(waveWidth, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(waveWidth / 2f)
        )
    }
}
