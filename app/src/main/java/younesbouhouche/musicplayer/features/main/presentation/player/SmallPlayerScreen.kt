package younesbouhouche.musicplayer.features.main.presentation.player

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import soup.compose.material.motion.animation.materialSharedAxisX
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.features.main.domain.models.QueueModel
import younesbouhouche.musicplayer.features.main.presentation.components.MyImage
import younesbouhouche.musicplayer.features.main.presentation.states.PlayState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SmallPlayerScreen(
    queue: QueueModel,
    state: PlayState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onImageLoad: (Bitmap?) -> Unit = { },
    onExpand: () -> Unit = {},
) {
    val file = queue.getCurrentItem()
    val angle by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = if (state == PlayState.PLAYING) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = { it }),
            repeatMode = RepeatMode.Restart
        ),
    )
    Row(modifier
        .clickable(onClick = onExpand, enabled = enabled)
        .fillMaxWidth()
        .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyImage(
            model = file?.coverUri,
            icon = Icons.Default.MusicNote,
            iconTint = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
            modifier = Modifier.fillMaxHeight().aspectRatio(1f, true),
            shape = MaterialShapes.Cookie12Sided.toShape(angle.roundToInt()),
            background = MaterialTheme.colorScheme.surface.copy(0.5f),
            onSuccess = {
                onImageLoad((it.result.drawable as? BitmapDrawable)?.bitmap)
            },
            onError = {
                onImageLoad(null)
            }
        )
        AnimatedContent(
            queue.index,
            Modifier.weight(1f),
            transitionSpec = {
                materialSharedAxisX(forward = initialState < targetState, 100)
            },
        ) { index ->
            Column(
                Modifier.padding(vertical = 4.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    queue.items.getOrNull(index)?.title ?: "No song playing",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    queue.items.getOrNull(index)?.artist ?: "Unknown artist",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                )
            }
        }
        file?.let {
            ExpressiveIconButton(
                {
                    Image(
                        rememberAnimatedVectorPainter(
                            AnimatedImageVector.animatedVectorResource(R.drawable.play_to_pause_animation),
                            state == PlayState.PLAYING
                        ),
                        null,
                        Modifier.size(IconButtonDefaults.mediumIconSize),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primaryContainer)
                    )
                },
                size = IconButtonDefaults.mediumIconSize,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                ),
                enabled = enabled
            ) {
                onPlaybackEvent(PlaybackEvent.PauseResume)
            }
            ExpressiveIconButton(
                Icons.Default.Clear,
                size = IconButtonDefaults.mediumIconSize,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(.3f),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                enabled = enabled
            ) {
                onPlaybackEvent(PlaybackEvent.Stop)
            }
        }
    }
}