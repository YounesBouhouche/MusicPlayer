package younesbouhouche.musicplayer.main.presentation.player

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.events.TimerType
import younesbouhouche.musicplayer.main.domain.models.QueueModel
import younesbouhouche.musicplayer.main.presentation.player.components.ActionBar
import younesbouhouche.musicplayer.main.presentation.player.components.Carousel
import younesbouhouche.musicplayer.main.presentation.player.sheets.QueueSheet
import younesbouhouche.musicplayer.main.presentation.player.sheets.SpeedSheet
import younesbouhouche.musicplayer.main.presentation.player.sheets.TimerSheet
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.util.timeString
import younesbouhouche.musicplayer.ui.theme.AppTheme
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LargePlayerScreen(
    queue: QueueModel,
    playerState: PlayerState,
    modifier: Modifier = Modifier,
    onCollapse: () -> Unit,
    onSetFavorite: (String, Boolean) -> Unit,
    onSaveQueue: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onPlaybackEvent: (PlaybackEvent) -> Unit,
) {
    var queueSheetVisible by remember { mutableStateOf(false) }
    var speedSheetVisible by remember { mutableStateOf(false) }
    var timerVisible by remember { mutableStateOf(false) }
    val currentItem = queue.items.getOrNull(queue.index)
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    Column(modifier
        .systemBarsPadding()
        .padding(24.dp, 8.dp, 24.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ExpressiveIconButton(
                Icons.Default.ExpandMore,
                size = IconButtonDefaults.mediumIconSize,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(.3f),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.size(50.dp)
            ) {
                queueSheetVisible = true
            }
        }
        Carousel(
            queue,
            Modifier.weight(1f),
            playerState.playState == PlayState.PLAYING,
            onPlaybackEvent
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
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
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
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
                val iconSize = IconButtonDefaults.extraLargeIconSize
                repeat(3) {
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
                    val icon = @Composable {
                        when(it) {
                            0 ->
                                Icon(
                                    Icons.Default.SkipPrevious,
                                    null,
                                    Modifier.size(iconSize)
                                )
                            1 ->
                                Image(
                                    rememberAnimatedVectorPainter(
                                        AnimatedImageVector.animatedVectorResource(R.drawable.play_to_pause_animation),
                                        playerState.playState == PlayState.PLAYING
                                    ),
                                    null,
                                    Modifier.size(iconSize),
                                    colorFilter = ColorFilter.tint(contentColor)
                                )
                            else ->
                                Icon(
                                    Icons.Default.SkipNext,
                                    null,
                                    Modifier.size(iconSize)
                                )
                        }
                    }
                    ExpressiveIconButton(
                        icon,
                        modifier = Modifier
                            .height(100.dp)
                            .weight(weight),
                        size = iconSize,
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
        ActionBar(
            playerState.shuffle,
            playerState.repeatMode,
            playerState.speed,
            playerState.timer,
            {
                timerVisible = true
            },
            {
                speedSheetVisible = true
            },
            onPlaybackEvent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
    }
    TimerSheet(
        timerVisible,
        {
            timerVisible = false
        },
        playerState.timer,
        {
            onPlaybackEvent(PlaybackEvent.SetTimer(it))
        }
    )
    SpeedSheet(
        speedSheetVisible,
        {
            speedSheetVisible = false
        },
        playerState.speed,
        {
            onPlaybackEvent(PlaybackEvent.SetSpeed(it))
        }
    )
    AppTheme {
        QueueSheet(
            queueSheetVisible,
            {
                queueSheetVisible = false
            },
            queue,
            onPlaybackEvent,
            onSaveQueue,
            onAddToPlaylist
        )
    }
}

@Preview
@Composable
private fun LargePlayerPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            LargePlayerScreen(
                queue = QueueModel(
                    index = 0,
                    items = List(10) {
                        MusicCard.Builder()
                            .setId(it.toLong())
                            .setTitle("Song Title $it")
                            .setArtist("Artist Name")
                            .setAlbum("Album Name")
                            .setDuration(240000L)
                            .setPath("/storage/emulated/0/Music/song$it.mp3")
                            .setCoverUri(Uri.EMPTY)
                            .setFavorite(it % 2 == 0)
                            .build()
                    }
                ),
                playerState =
                    PlayerState(
                        playState = PlayState.PLAYING,
                        time = 60000,
                        loading = false,
                        shuffle = true,
                        repeatMode = Player.REPEAT_MODE_ALL,
                        timer = TimerType.Disabled
                    ),
                onCollapse = {},
                onSetFavorite = { _, _ -> },
                onPlaybackEvent = {}
            )
        }
    }
}