package younesbouhouche.musicplayer.features.settings.presentation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppIcon(modifier: Modifier = Modifier) {
    val view = LocalView.current
    var angle by remember {
        mutableIntStateOf(0)
    }
    val animatedAngle by animateIntAsState(targetValue = angle, label = "")
    Surface(
        modifier.size(200.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialShapes.Cookie12Sided.toShape(animatedAngle)
    ) {
        Box(
            modifier.fillMaxSize().clickable {
                angle += 30
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.foreground),
                null,
                Modifier.fillMaxSize(.4f),
                MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
