package younesbouhouche.musicplayer.features.main.presentation.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.younesb.mydesignsystem.presentation.components.Image
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.presentation.theme.AppTheme

@Composable
fun PictureCard(
    picture: Any?,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alternatives: List<Any?> = emptyList(),
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
            Box(
                Modifier.fillMaxWidth().aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                picture?.let {
                    Image(
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
                        },
                        onError = {
                            palette.reset()
                        }
                    )
                } ?: alternatives.takeIf { it.isNotEmpty() }?.let { alts ->
                    ImagesGrid(
                        images = alts,
                        icon = icon,
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        itemIcon = Icons.Default.MusicNote
                    )
                } ?: Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(.5f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(
                    Modifier
                        .clickable(onClick = onClick)
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                .75f to MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .8f)
                            )
                        )
                        .padding(contentPadding),
                ) {
                    ProvideTextStyle(
                        MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
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

@Composable
internal fun ImagesGrid(
    images: List<Any?>,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    itemIcon: ImageVector = icon,
) {
    val imgs = images.take(4)

    Box(modifier = modifier) {
        when (imgs.size) {
            0 -> Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(.5f).align(Alignment.Center),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            1 -> Image(
                imgs[0],
                itemIcon,
                Modifier.fillMaxSize()
            )
            2 -> {
                Image(
                    imgs[0],
                    itemIcon,
                    Modifier
                        .fillMaxSize(.65f)
                        .align(Alignment.TopStart)
                )
                Image(
                    imgs[1],
                    itemIcon,
                    Modifier
                        .fillMaxSize(.65f)
                        .align(Alignment.BottomEnd)
                )
            }
            3 -> {
                Image(
                    imgs[0],
                    itemIcon,
                    Modifier
                        .fillMaxSize(.6f)
                        .align(Alignment.TopStart)
                )
                Image(
                    imgs[1],
                    itemIcon,
                    Modifier
                        .fillMaxSize(.45f)
                        .align(Alignment.TopEnd)
                )
                Image(
                    imgs[2],
                    itemIcon,
                    Modifier
                        .fillMaxSize(.45f)
                        .align(Alignment.BottomCenter)
                )
            }
            else -> {
                // 4 images in corners
                Image(
                    imgs[0],
                    itemIcon,
                    Modifier
                        .fillMaxSize(.48f)
                        .align(Alignment.TopStart)
                )
                Image(
                    imgs[1],
                    itemIcon,
                    Modifier
                        .fillMaxSize(.48f)
                        .align(Alignment.TopEnd)
                )
                Image(
                    imgs[2],
                    itemIcon,
                    Modifier
                        .fillMaxSize(.48f)
                        .align(Alignment.BottomStart)
                )
                Image(
                    imgs[3],
                    itemIcon,
                    Modifier
                        .fillMaxSize(.48f)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}