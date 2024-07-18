package younesbouhouche.musicplayer.main.presentation.player

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.Player
import kotlinx.coroutines.launch
import sh.calvin.reorderable.rememberReorderableLazyListState
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.SwipeMusicCardLazyItem
import younesbouhouche.musicplayer.core.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.TimerType
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.states.UiState
import younesbouhouche.musicplayer.main.presentation.util.timeString
import younesbouhouche.musicplayer.main.presentation.util.timerString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Queue(
    queue: List<MusicCard>,
    index: Int,
    uiState: UiState,
    playerState: PlayerState,
    lyrics: Boolean,
    playlistHidden: Boolean,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val listState = rememberLazyListState()
    val reorderableState =
        rememberReorderableLazyListState(listState) { from, to ->
            onPlayerEvent(PlayerEvent.Swap(from.index, to.index))
            view.performHapticFeedback(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                } else {
                    HapticFeedbackConstants.GESTURE_END
                },
            )
        }
    Box(modifier) {
        BottomAppBar(
            contentPadding = PaddingValues(horizontal = 8.dp),
            actions = {
                FilledToggleIconButton(
                    playerState.repeatMode != Player.REPEAT_MODE_OFF,
                    { onPlayerEvent(PlayerEvent.CycleRepeatMode) },
                    if (playerState.repeatMode == Player.REPEAT_MODE_ONE) {
                        Icons.Default.RepeatOne
                    } else {
                        Icons.Default.Repeat
                    },
                )
                FilledToggleIconButton(
                    playerState.shuffle,
                    { onPlayerEvent(PlayerEvent.ToggleShuffle) },
                    Icons.Default.Shuffle,
                )
                FilledToggleIconButton(
                    playerState.speed != 1f,
                    {
                        if (playerState.speed == 1f) {
                            onUiEvent(UiEvent.ShowSpeedDialog)
                        } else {
                            onPlayerEvent(PlayerEvent.ResetSpeed)
                        }
                    },
                    Icons.Default.Speed,
                )
                if (uiState.showPitch)
                    FilledToggleIconButton(
                        playerState.pitch != 1f,
                        {
                            if (playerState.pitch == 1f) onUiEvent(UiEvent.ShowPitchDialog)
                            else onPlayerEvent(PlayerEvent.SetPitch(1f))
                        },
                        Icons.Default.RecordVoiceOver,
                    )
                FilledToggleIconButton(
                    playerState.timer != TimerType.Disabled,
                    {
                        if (playerState.timer == TimerType.Disabled) {
                            onUiEvent(UiEvent.ShowTimerDialog)
                        } else {
                            onPlayerEvent(PlayerEvent.SetTimer(TimerType.Disabled))
                        }
                    },
                ) {
                    AnimatedContent(targetState = playerState.timer, label = "") {
                        when (it) {
                            TimerType.Disabled -> Icon(Icons.Default.Timer, null)
                            is TimerType.End -> Text("${it.tracks}")
                            is TimerType.Time ->
                                Text(
                                    ((it.hour * 60 + it.min) * 60000L).timeString.dropLast(3),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            is TimerType.Duration ->
                                Text(
                                    it.ms.timerString,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                        }
                    }
                }
                FilledToggleIconButton(
                    lyrics,
                    { onUiEvent(UiEvent.ToggleLyrics) },
                    Icons.Default.Lyrics,
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onUiEvent(UiEvent.ExpandPlaylist) },
                ) {
                    Icon(Icons.AutoMirrored.Filled.PlaylistPlay, null)
                }
            },
            modifier =
                Modifier
                    .zIndex(if (playlistHidden) 2f else 1f)
                    .alpha(1f - progress)
                    .align(Alignment.TopStart),
        )
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CenterAlignedTopAppBar(
                    colors =
                        TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                    title = {
                        Text(text = stringResource(R.string.up_next))
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { onUiEvent(UiEvent.CollapsePlaylist) },
                            modifier = Modifier.padding(start = 12.dp),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                onUiEvent(UiEvent.ShowQueueBottomSheet)
                            },
                            colors =
                                IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground,
                                ),
                            modifier = Modifier.padding(start = 12.dp),
                        ) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                    },
                )
            },
            modifier =
                Modifier
                    .alpha(progress)
                    .fillMaxSize()
                    .align(Alignment.TopStart)
                    .zIndex(if (playlistHidden) 1f else 2f),
        ) { paddingValues ->
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                state = listState,
            ) {
                items(queue, key = { it.id }) { card ->
                    val item by rememberUpdatedState(card)
                    val idx = queue.indexOf(card)
                    val dismissState =
                        rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                value == SwipeToDismissBoxValue.EndToStart
                            },
                            positionalThreshold = { it / 1.5f },
                        )
                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                        onPlayerEvent(PlayerEvent.Remove(queue.indexOf(item)))
                        LaunchedEffect(Unit) {
                            launch { dismissState.reset() }
                        }
                    }
                    SwipeMusicCardLazyItem(
                        state = dismissState,
                        file = item,
                        reorderableState = reorderableState,
                        swipingItemBackground =
                            if (index == idx) {
                                MaterialTheme.colorScheme.background
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            },
                    ) { onPlayerEvent(PlayerEvent.Seek(idx, 0L)) }
                }
                item {
                    Spacer(Modifier.height(navBarHeight))
                }
            }
        }
    }
}

@Composable
private fun FilledToggleIconButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
) {
    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors =
            IconButtonDefaults.filledIconToggleButtonColors().copy(
                containerColor = Color.Transparent,
            ),
    ) {
        Icon(
            icon,
            null,
        )
    }
}

@Composable
private fun FilledToggleIconButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: @Composable () -> Unit,
) {
    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors =
            IconButtonDefaults.filledIconToggleButtonColors().copy(
                containerColor = Color.Transparent,
            ),
        content = icon,
    )
}
