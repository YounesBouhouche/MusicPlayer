package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MusicCardListItem(
    file: MusicCard,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    dragging: Boolean = false,
    shape: Shape = MaterialTheme.shapes.large,
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    trailingContent: @Composable RowScope.() -> Unit = { },
    onLongClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    onClick: () -> Unit = { },
) {
    val scale by animateFloatAsState(
        if (dragging) 1.05f else 1f
    )
    val shadow by animateDpAsState(
        if (dragging) 16.dp else 0.dp
    )
    val angle by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = { it }),
            repeatMode = RepeatMode.Restart
        ),
    )
    val itemShape = if (active) RoundedCornerShape(100) else shape
    val state = rememberSwipeToDismissBoxState { .5f }
    SwipeToDismissBox(
        state,
        backgroundContent = {
            if (onDismiss != null)
                AnimatedVisibility(state.progress > 0f) {
                    Box(
                        Modifier
                            .clip(itemShape)
                            .background(MaterialTheme.colorScheme.error)
                            .fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            null,
                            Modifier.size(24.dp).offset(x = -(24.dp)),
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }
        },
        onDismiss = {
            onDismiss?.invoke()
        },
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = onDismiss != null,
        gesturesEnabled = onDismiss != null
    ) {
        ListItem(
            onClick,
            modifier.scale(scale).shadow(shadow, shape),
            onLongClick,
            shape = itemShape,
            background =
                if (active) MaterialTheme.colorScheme.primaryContainer.copy(.4f).compositeOver(MaterialTheme.colorScheme.surfaceContainerLow)
                else MaterialTheme.colorScheme.surfaceContainerLow,
            leadingContent = {
                leadingContent?.invoke(this)
                MyImage(
                    model = file.coverUri,
                    icon = Icons.Default.MusicNote,
                    modifier = Modifier.size(58.dp),
                    iconTint =
                        if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    shape =
                        if (active) MaterialShapes.Cookie9Sided.toShape(angle.roundToInt())
                        else MaterialTheme.shapes.large,
                    background =
                        if (active) MaterialTheme.colorScheme.onPrimaryContainer.copy(0.3f)
                        else MaterialTheme.colorScheme.surface.copy(0.8f)
                    ,
                )
            },
            trailingContent = {
                if (active)
                    PlaybackAnimation(
                        Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                trailingContent(this)
            }
        ) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    file.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    color = if (active) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        file.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.MiddleEllipsis,
                        color =
                            if (active) MaterialTheme.colorScheme.primary.copy(0.7f)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}