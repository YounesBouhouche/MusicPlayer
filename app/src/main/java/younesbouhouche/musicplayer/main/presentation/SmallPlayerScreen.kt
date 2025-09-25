package younesbouhouche.musicplayer.main.presentation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SmallPlayerScreen(
    file: MusicCard?,
    state: PlayState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    onImageLoad: (Bitmap?) -> Unit = { },
    onExpand: () -> Unit = {},
) {
    val angle by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
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
            }
        )
        Column(Modifier.weight(1f)
            .padding(vertical = 4.dp)
            .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                file?.title ?: "No song playing",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                file?.artist ?: "Unknown artist",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
            )
        }
        file?.let {
            ExpressiveIconButton(
                when (state) {
                    PlayState.PLAYING -> Icons.Default.Pause
                    PlayState.PAUSED, PlayState.STOP -> Icons.Default.PlayArrow
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