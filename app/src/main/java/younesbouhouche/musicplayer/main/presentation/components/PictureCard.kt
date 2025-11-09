package younesbouhouche.musicplayer.main.presentation.components

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
    shape: Shape = MaterialTheme.shapes.extraLarge,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val palette = rememberPaletteState()
    val scope = rememberCoroutineScope()
    AppTheme(palette) {
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = modifier,
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
                    Modifier.clickable(onClick = onClick).fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                .75f to MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        .padding(contentPadding),
                ) {
                    ProvideTextStyle(
                        MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        content()
                    }
                }
            }
        }
    }
}