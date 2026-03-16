package younesbouhouche.musicplayer.features.settings.presentation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreenIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    var hasAppeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hasAppeared = true
    }

    val appearanceScale by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "settingsIconAppearanceScale"
    )
    val appearanceRotation by animateFloatAsState(
        targetValue = if (hasAppeared) 0f else -180f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "settingsIconAppearanceRotation"
    )

    var angle by remember { mutableIntStateOf(0) }
    val animatedAngle by animateIntAsState(targetValue = angle, label = "settingsIconTapRotation")

    Surface(
        modifier = modifier
            .size(140.dp)
            .graphicsLayer {
                scaleX = appearanceScale
                scaleY = appearanceScale
                rotationZ = appearanceRotation
            },
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialShapes.Cookie12Sided.toShape(animatedAngle)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().clickable {
                angle += 30
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                null,
                Modifier.fillMaxSize(.4f),
                MaterialTheme.colorScheme.secondary,
            )
        }
    }
}
