package younesbouhouche.musicplayer.dialog.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.util.timeString
import kotlin.math.roundToLong


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogContent(
    card: MusicCard?,
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
            Modifier.padding(18.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(64.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                ) {
                    card?.cover?.let {
                        Image(
                            it.asImageBitmap(),
                            contentDescription = null,
                            Modifier.fillMaxSize(),
                        )
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        card?.title ?: "",
                        maxLines = 2,
                        style = MaterialTheme.typography.titleSmall,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        card?.artist ?: "",
                        Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                AnimatedContent(
                    state.loading,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "",
                ) {
                    if (it) {
                        CircularProgressIndicator(Modifier.size(48.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(pauseResume) {
                            Icon(
                                if (state.playState == PlayState.PLAYING) {
                                    Icons.Default.Pause
                                } else {
                                    Icons.Default.PlayArrow
                                },
                                null,
                            )
                        }
                    }
                }
            }
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
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
                ) {
                    if (it == null) {
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
    DialogContent(MusicCard.Builder().build(), PlayerState(), {}, {})
}
