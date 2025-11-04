package younesbouhouche.musicplayer.main.presentation.components

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.settings.presentation.SettingsActivity
import younesbouhouche.musicplayer.settings.presentation.routes.about.AboutActivity
import younesbouhouche.musicplayer.ui.theme.AppTheme

@Composable
fun DrawerSheetContent(
    state: DrawerState,
    currentFile: MusicCard?
) {
    val paletteState = rememberPaletteState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    AppTheme(paletteState.palette) {
        ModalDrawerSheet(
            state,
            windowInsets = DrawerDefaults.windowInsets.exclude(WindowInsets.systemBars)
        ) {
            Column(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow).fillMaxSize()) {
                Box(Modifier.fillMaxWidth().weight(1f)) {
                    MyImage(
                        model = currentFile?.coverUri,
                        icon = Icons.Default.Album,
                        modifier = Modifier.fillMaxSize(),
                        shape = RectangleShape,
                        background = MaterialTheme.colorScheme.surfaceVariant,
                        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                        onSuccess = {
                            (it.result.drawable as? BitmapDrawable)?.bitmap?.asImageBitmap()?.let { imageBitmap ->
                                scope.launch {
                                    paletteState.generate(imageBitmap)
                                }
                            } ?: paletteState.reset()
                        }
                    )
                    Box(Modifier
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color.Transparent,
                                1.0f to MaterialTheme.colorScheme.surfaceContainerLow,
                            )
                        )
                        .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                currentFile?.title ?: "No song playing",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                currentFile?.artist ?: "Unknown artist",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Box(Modifier.fillMaxSize().weight(1f)) {
                    // Animated Material 3 Expressive Shapes
                    AnimatedExpressiveShapes()
                    Column(
                        Modifier.fillMaxSize().padding(8.dp, 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text(stringResource(R.string.settings)) },
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        context,
                                        SettingsActivity::class.java
                                    )
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            selected = false,
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Info, contentDescription = null) },
                            label = { Text(stringResource(R.string.about)) },
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        context,
                                        AboutActivity::class.java
                                    )
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            selected = false,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnimatedExpressiveShapes() {
    val infiniteTransition = rememberInfiniteTransition(label = "shapes")

    // Animate rotation angles for different shapes
    val angle1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle1"
    )

    val angle2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle2"
    )

    val angle3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle3"
    )

    val angle4 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle4"
    )

    // Floating animation for vertical movement
    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val floatOffset3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float3"
    )

    Box(modifier = Modifier.blur(6.dp).fillMaxSize().alpha(.2f)) {
        // Shape 1 - Top Left
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = 30.dp, y = 40.dp + floatOffset1.dp)
                .align(Alignment.TopStart)
                .rotate(angle1)
                .clip(MaterialShapes.Arrow.toShape())
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
        )

        // Shape 2 - Top Right
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = (-20).dp, y = 60.dp + floatOffset2.dp)
                .align(Alignment.TopEnd)
                .rotate(angle2)
                .clip(MaterialShapes.Cookie12Sided.toShape())
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
        )

        // Shape 3 - Center
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(y = floatOffset3.dp)
                .align(Alignment.Center)
                .rotate(angle3)
                .clip(MaterialShapes.Arch.toShape())
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f))
        )

        // Shape 4 - Bottom Left
        Box(
            modifier = Modifier
                .size(90.dp)
                .offset(x = 40.dp, y = (-30).dp + floatOffset1.dp)
                .align(Alignment.BottomStart)
                .rotate(angle4)
                .clip(MaterialShapes.Bun.toShape())
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f))
        )

        // Shape 5 - Bottom Right
        Box(
            modifier = Modifier
                .size(110.dp)
                .offset(x = (-25).dp, y = (-40).dp + floatOffset2.dp)
                .align(Alignment.BottomEnd)
                .rotate((angle1 * 0.8f))
                .clip(MaterialShapes.Gem.toShape())
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f))
        )
    }
}