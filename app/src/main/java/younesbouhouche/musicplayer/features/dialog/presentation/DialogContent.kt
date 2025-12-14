package younesbouhouche.musicplayer.features.dialog.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import com.younesb.mydesignsystem.presentation.components.Image
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.player.domain.models.PlayState
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState
import younesbouhouche.musicplayer.features.main.presentation.util.timeString
import kotlin.math.roundToLong


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DialogContent(
    card: Song?,
    state: PlayerState,
    pauseResume: () -> Unit,
    seekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dragging by remember { mutableStateOf(false) }
    var value by remember { mutableFloatStateOf(0f) }
    val interactionSource = remember { MutableInteractionSource() }
    Surface(modifier.clip(MaterialTheme.shapes.large)) {
        Column(
            Modifier.padding(24.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    model = card?.coverPath,
                    icon = Icons.Default.MusicNote,
                    shape = MaterialTheme.shapes.large,
                    background = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.size(120.dp)
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        card?.title ?: "",
                        maxLines = 2,
                        style = MaterialTheme.typography.headlineMedium,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        card?.artist ?: "",
                        Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(Modifier.fillMaxWidth()) {
                ExpressiveIconButton(
                    {
                        Icon(
                            rememberAnimatedVectorPainter(
                                AnimatedImageVector.animatedVectorResource(R.drawable.play_to_pause_animation),
                                state.playState == PlayState.PLAYING
                            ),
                            null,
                            Modifier.size(IconButtonDefaults.largeIconSize)
                        )
                    },
                    loading = state.loading,
                    onClick = pauseResume,
                    size = IconButtonDefaults.largeIconSize,
                    modifier = Modifier.fillMaxWidth(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )
            }
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        state.time.timeString,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        (card?.duration ?: 0L).timeString,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                AnimatedContent(
                    card,
                    Modifier.fillMaxWidth().height(30.dp),
                    label = "",
                    contentAlignment = Alignment.CenterStart,
                ) { musicCard ->
                    if (musicCard == null) {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            LinearProgressIndicator(Modifier.fillMaxWidth())
                        }
                    } else {
                        Slider(
                            interactionSource = interactionSource,
                            modifier = Modifier.fillMaxWidth().height(30.dp),
                            enabled = !state.loading,
                            thumb = {
                                SliderDefaults.Thumb(
                                    interactionSource = interactionSource,
                                    colors = SliderDefaults.colors(),
                                    thumbSize = DpSize(4.dp, 30.dp),
                                )
                            },
                            value =
                            if (dragging or state.loading) {
                                value
                            } else if (card == null) {
                                0f
                            } else if (card.duration == 0L) {
                                0f
                            } else {
                                state.time / card.duration.toFloat()
                            },
                            onValueChange = {
                                dragging = true
                                value = it
                            },
                            onValueChangeFinished = {
                                dragging = false
                                seekTo((value * (card?.duration ?: 0L)).roundToLong())
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DialogContentPreview() {
    DialogContent(
        Song.Builder()
            .id(1L)
            .title("Song Title")
            .artist("Artist Name")
            .duration(240000L)
            .build(),
        PlayerState(),
        {},
        {}
    )
}
