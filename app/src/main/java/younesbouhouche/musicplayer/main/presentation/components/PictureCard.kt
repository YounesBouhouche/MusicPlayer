package younesbouhouche.musicplayer.main.presentation.components

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.ui.theme.AppTheme

@Composable
fun PictureCard(
    picture: Any?,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val palette = rememberPaletteState()
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val corners by animateDpAsState(
        if (pressed) 8.dp else 24.dp
    )
    AppTheme(palette.palette) {
        Card(
            shape = RoundedCornerShape(corners),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            onClick = onClick,
            modifier = modifier,
            interactionSource = interactionSource
        ) {
            Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
                MyImage(
                    picture,
                    icon,
                    Modifier.fillMaxSize(),
                    background = Color.Transparent,
                    onSuccess = {
                        ((it.result.drawable as? BitmapDrawable)?.bitmap)?.let { bitmap ->
                            scope.launch {
                                palette.generate(bitmap.asImageBitmap())
                            }
                        } ?: palette.reset()
                    }
                )
                Box(
                    Modifier.fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                .75f to MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        .padding(16.dp),
                ) {
                    ProvideTextStyle(
                        MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        content()
                    }
                }
            }
        }
    }
}