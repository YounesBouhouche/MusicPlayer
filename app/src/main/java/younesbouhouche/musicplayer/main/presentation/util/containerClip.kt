package younesbouhouche.musicplayer.main.presentation.util

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

@Composable
fun Modifier.containerClip(
    background: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = expressiveRectShape(0, 2)
) = this.clip(shape).background(background)