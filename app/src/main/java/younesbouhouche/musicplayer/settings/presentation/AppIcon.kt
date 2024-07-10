package younesbouhouche.musicplayer.settings.presentation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R

@Composable
fun AppIcon(modifier: Modifier = Modifier) {
    val view = LocalView.current
    var angle by remember {
        mutableFloatStateOf(0f)
    }
    val animatedAngle by animateFloatAsState(targetValue = angle, label = "")
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier.size(300.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                ImageVector.vectorResource(id = R.drawable.material_3_flower),
                null,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .clickable {
                            angle += 30f
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }
                        .rotate(animatedAngle),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceContainer),
            )
            Icon(
                Icons.Default.MusicNote,
                null,
                Modifier.fillMaxSize(.5f),
                MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
