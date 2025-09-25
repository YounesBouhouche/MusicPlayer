package younesbouhouche.musicplayer.main.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.media3.common.Player
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.events.TimerType
import younesbouhouche.musicplayer.main.domain.models.QueueModel
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.util.timeString
import younesbouhouche.musicplayer.ui.theme.AppTheme
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LargePlayerScreen(
    queue: QueueModel,
    playerState: PlayerState,
    modifier: Modifier = Modifier,
    onCollapse: () -> Unit,
    onSetFavorite: (String, Boolean) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit
) {
    var queueSheetVisible by remember { mutableStateOf(false) }
    val currentItem = queue.items.getOrNull(queue.index)
    val pagerState = rememberPagerState {
        queue.items.size
    }
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    LaunchedEffect(queue) {
        pagerState.animateScrollToPage(queue.index)
    }
    Column(modifier
        .systemBarsPadding()
        .padding(24.dp, 8.dp, 24.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ExpressiveIconButton(
                Icons.Default.ExpandMore,
                size = IconButtonDefaults.mediumIconSize,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(.3f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.size(50.dp),
                onClick = onCollapse
            )
            Text(
                stringResource(R.string.now_playing),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            ExpressiveIconButton(
                Icons.AutoMirrored.Filled.QueueMusic,
                size = IconButtonDefaults.mediumIconSize,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(.3f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.size(50.dp)
            ) {
                queueSheetVisible = true
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.clip(MaterialTheme.shapes.extraLarge)
                .weight(1f)
                .aspectRatio(1f, true),
            contentPadding = PaddingValues(horizontal = 16.dp),
            snapPosition = SnapPosition.Center,
            key = { queue.items[it].id },
        ) { page ->
            val transition = rememberInfiniteTransition(label = "Playing animation")
            val animatedScale by transition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(5000),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "Scale animation",
            )
            val diskScale by animateFloatAsState(
                targetValue =
                    if ((playerState.playState == PlayState.PLAYING) and (queue.index == page))
                        animatedScale
                    else 1f,
                label = "",
            )
            val pageOffset =
                (
                        (pagerState.currentPage - page) +
                                pagerState
                                    .currentPageOffsetFraction
                        ).absoluteValue
            with(queue.items[page]) {
                MyImage(
                    model = coverUri,
                    icon = Icons.Default.MusicNote,
                    iconTint = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
                    shape = MaterialTheme.shapes.extraLarge,
                    background = MaterialTheme.colorScheme.surface.copy(
                        if (pageOffset < 0.5f) 1 - pageOffset else 0.5f
                    ),
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f, true)
                        .scale(lerp(0.8f, 1f, 1 - pageOffset))
                        .scale(diskScale),
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        currentItem?.title ?: "Not playing",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        currentItem?.artist ?: "Unknown artist",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
                    )
                }
                ExpressiveIconButton(
                    icon =
                        if (currentItem?.favorite == true) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                    size = IconButtonDefaults.largeIconSize,
                    modifier = Modifier.size(72.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    currentItem?.let {
                        onSetFavorite(it.path, !it.favorite)
                    }
                }
            }
            Column(
                Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Slider(
                    value =
                        if ((dragging) or (playerState.loading)) {
                            sliderValue
                        } else {
                            (playerState.time.toFloat() / (currentItem?.duration ?: 1))
                        },
                    onValueChange = {
                        sliderValue = it
                        dragging = true
                    },
                    onValueChangeFinished = {
                        dragging = false
                        onPlaybackEvent(PlaybackEvent.SeekTime((sliderValue * (currentItem?.duration ?: 0)).roundToLong()))
                    },
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(.3f),
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTickColor = MaterialTheme.colorScheme.primary,
                        inactiveTickColor = MaterialTheme.colorScheme.onSurface.copy(.3f),
                    )
                )
                Row(Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    ProvideTextStyle(
                        MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(playerState.time.timeString)
                        Text((currentItem?.duration ?: 0).timeString)
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(3) {
                    val icon = when(it) {
                        0 -> Icons.Default.SkipPrevious
                        1 ->
                            if (playerState.playState == PlayState.PLAYING) Icons.Default.Pause
                            else Icons.Default.PlayArrow
                        else -> Icons.Default.SkipNext
                    }
                    val interactionSource = remember { MutableInteractionSource() }
                    val pressed by interactionSource.collectIsPressedAsState()
                    val weight by animateFloatAsState(
                        if (pressed) 1.4f else 1f
                    )
                    val contentColor =
                        if (it == 1) MaterialTheme.colorScheme.onPrimary
                        else  MaterialTheme.colorScheme.onPrimaryContainer
                    val containerColor =
                        if (it == 1) MaterialTheme.colorScheme.primary
                        else  MaterialTheme.colorScheme.surface.copy(alpha = .4f)
                    ExpressiveIconButton(
                        icon,
                        modifier = Modifier.height(100.dp).weight(weight),
                        size = IconButtonDefaults.extraLargeIconSize,
                        interactionSource = interactionSource,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = containerColor,
                            contentColor = contentColor,
                        )
                    ) {
                        onPlaybackEvent(when(it) {
                            0 -> PlaybackEvent.Previous
                            1 -> PlaybackEvent.PauseResume
                            else -> PlaybackEvent.Next
                        })
                    }
                }
            }
        }
        Surface(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.surface.copy(.4f),
            shape = RoundedCornerShape(100),
        ) {
            FlowRow(
                Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                repeat(3) {
                    val icon = when(it) {
                        0 ->
                            if (playerState.shuffle) Icons.Default.ShuffleOn
                            else Icons.Default.Shuffle
                        1 -> when(playerState.repeatMode) {
                            Player.REPEAT_MODE_ALL -> Icons.Default.RepeatOn
                            Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOneOn
                            else -> Icons.Default.Repeat
                        }
                        else -> Icons.Default.Timer
                    }
                    val active = when(it) {
                        0 -> playerState.shuffle
                        1 -> playerState.repeatMode != Player.REPEAT_MODE_OFF
                        else -> playerState.timer != TimerType.Disabled
                    }
                    val interactionSource = remember { MutableInteractionSource() }
                    val pressed by interactionSource.collectIsPressedAsState()
                    val weight by animateFloatAsState(
                        if (pressed) 1.4f else 1f
                    )
                    ToggleButton(
                        checked = active,
                        onCheckedChange = { checked ->
                            when(it) {
                                0 -> onPlaybackEvent(PlaybackEvent.ToggleShuffle)
                                1 -> onPlaybackEvent(PlaybackEvent.CycleRepeatMode)
                                else -> Unit
                            }
                        },
                        shapes =
                            if (it == 0)
                                ButtonGroupDefaults.connectedLeadingButtonShapes()
                            else if (it < 2)
                                ButtonGroupDefaults.connectedMiddleButtonShapes()
                            else ButtonGroupDefaults.connectedTrailingButtonShapes(),
                        modifier = Modifier
                            .heightIn(ButtonDefaults.MediumContainerHeight)
                            .semantics { role = Role.Checkbox }
                            .weight(weight),
                        contentPadding = ButtonDefaults
                            .contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(.1f)
                        ),
                        interactionSource = interactionSource,
                    ) {
                        Icon(icon, null)
                    }
                }
            }
        }
    }
    AppTheme {
        QueueSheet(
            queueSheetVisible,
            {
                queueSheetVisible = false
            },
            queue,
            onPlaybackEvent
        )
    }
}