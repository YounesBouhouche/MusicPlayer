package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZ
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmptyContainer(
    isEmpty: Boolean,
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable ColumnScope.() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    val angle by rememberInfiniteTransition().animateFloat(0f, 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = { it }),
            repeatMode = RepeatMode.Restart
        )
    )
    AnimatedContent(
        isEmpty,
        modifier,
        { materialSharedAxisZ(true) }
    ) {
        if (it) {
            Column(
                Modifier.padding(16.dp).padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
            ) {
                Box(
                    Modifier
                        .clip(MaterialShapes.Cookie12Sided.toShape(
                            angle.roundToInt())
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .height(220.dp)
                        .aspectRatio(1f, true)
                        .weight(1f, false),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxSize(.4f)
                    )
                }
                Text(
                    text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    softWrap = true,
                )
                trailingContent?.invoke(this)
            }
        } else {
            content()
        }
    }
}