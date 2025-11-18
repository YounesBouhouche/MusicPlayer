package younesbouhouche.musicplayer.features.main.data

import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.player.PlayerManager
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.core.domain.player.QueueManager
import younesbouhouche.musicplayer.core.domain.session.MediaSessionManager
import younesbouhouche.musicplayer.features.main.data.models.Queue
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.AddToQueue
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Backward
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.ClearQueue
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.CycleRepeatMode
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.DecreaseVolume
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Forward
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.IncreaseVolume
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Next
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Pause
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.PauseResume
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Play
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.PlayNext
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Previous
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Remove
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.ResetSpeed
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Resume
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Seek
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.SeekTime
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.SetPitch
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.SetPlayerVolume
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.SetRepeatMode
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.SetSpeed
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.SetTimer
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.SetVolume
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Stop
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.Swap
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent.ToggleShuffle
import younesbouhouche.musicplayer.features.main.domain.events.TimerType
import younesbouhouche.musicplayer.features.main.domain.repo.PlaybackRepository
import younesbouhouche.musicplayer.features.main.presentation.states.PlayState
import younesbouhouche.musicplayer.features.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.Task
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.roundToInt

class PlaybackRepositoryImpl(
    val playerManager: PlayerManager,
    val dataStore: PlayerDataStore,
    val mediaSessionManager: MediaSessionManager,
    val stateManager: PlayerStateManager,
    val queueManager: QueueManager
) : PlaybackRepository {
    private val _queue = queueManager.getQueue().map { it ?: Queue() }

    private val state = stateManager.playerState

    // Timer
    private var timerTask = Task()

    private val _queueList = _queue.map { it.items }
    private val _queueIndex = _queue.map { it.index }

    override fun initialize() {
        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            mediaSessionManager.initialize()
            if (stateManager.playerState.value.playState != PlayState.STOP)
                playerManager.startTimeUpdate()
        }
    }

    @OptIn(UnstableApi::class)
    override suspend fun onEvent(event: PlaybackEvent) {
        if (event == PlaybackEvent.Initialize) initialize()
        else
            playerManager.getPlayer().let { player ->
                when(event) {
                    is Play -> {
                        if ((_queueList.first() == event.items) and (state.value.playState != PlayState.STOP)) {
                            if (event.index != _queueIndex.first())
                                onEvent(Seek(event.index, 0))
                        } else {
                            if (mediaSessionManager.getSession() == null) initialize()
                            queueManager.setQueue(Queue(items = event.items.map { it.id }, index = event.index))
                            playerManager.play(
                                event.items,
                                event.index,
                                event.time,
                                shuffleMode = event.shuffle
                            )
                        }
                    }

                    is Backward -> {
                        player.seekBack()
                    }

                    CycleRepeatMode -> {
                        player.repeatMode = when(player.repeatMode) {
                            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                            else -> Player.REPEAT_MODE_OFF
                        }
                    }

                    DecreaseVolume -> {
                        player.decreaseDeviceVolume(0)
                    }

                    is Forward -> {
                        player.seekForward()
                    }

                    IncreaseVolume -> {
                        player.increaseDeviceVolume(0)
                    }

                    Next -> {
                        player.seekToNext()
                    }

                    Pause -> {
                        player.pause()
                    }

                    PauseResume -> {
                        if (player.isPlaying) player.pause()
                        else player.play()
                    }

                    Previous -> {
                        player.seekToPrevious()
                    }

                    is Remove -> {
                        player.removeMediaItem(event.index)
                        queueManager.removeAt(event.index)
                        queueManager.updateIndex(player.currentMediaItemIndex)
                        if (player.mediaItemCount == 0)
                            onEvent(Stop)
                    }

                    ResetSpeed -> {
                        player.setPlaybackSpeed(1f)
                    }

                    Resume -> {
                        player.play()
                    }

                    is Seek -> {
                        playerManager.seek(event.index, event.time, event.skipIfSameIndex)
                    }

                    is SeekTime -> {
                        player.seekTo(event.time)
                    }

                    is SetPitch -> {
                        player.playbackParameters = player.playbackParameters.withPitch(event.pitch)
                    }

                    is SetPlayerVolume -> {
                        player.volume = event.volume
                    }

                    is SetRepeatMode -> {
                        player.repeatMode = event.repeatMode
                    }

                    is SetSpeed -> {
                        player.setPlaybackSpeed(event.speed)
                    }

                    is SetTimer -> {
                        timerTask.stop()
                        event.timer.let { timer ->
                            stateManager.updateState { state -> state.copy(timer = timer) }
                            timerTask.start {
                                when (timer) {
                                    is TimerType.Duration -> {
                                        while ((state.value.timer as TimerType.Duration).ms > 0) {
                                            delay(1000L)
                                            stateManager.updateState { state ->
                                                state.copy(
                                                    timer =
                                                        TimerType.Duration(
                                                            (state.timer as TimerType.Duration).ms - 1000L,
                                                        )
                                                )
                                            }
                                        }
                                        onEvent(Stop)
                                    }

                                    is TimerType.Time -> {
                                        timerTask.startRepeating(1000L) {
                                            val time = with(LocalDateTime.now()) { hour * 60 + minute }
                                            if (abs(time - (timer.hour * 60 + timer.min)) == 0)
                                                timerTask.stop()
                                        }
                                        onEvent(Stop)
                                    }

                                    else -> return@start
                                }
                            }
                        }
                    }

                    is SetVolume -> {
                        player.setDeviceVolume(
                            (event.volume * (player.deviceInfo.maxVolume - player.deviceInfo.minVolume))
                                .roundToInt(),
                            0
                        )
                    }

                    Stop -> {
                        player.stop()
                        player.clearMediaItems()
                        timerTask.stop()
                        queueManager.updateQueue(Queue())
                        stateManager.updateState {
                            it.copy(playState = PlayState.STOP, timer = TimerType.Disabled)
                        }
                    }

                    ClearQueue -> {
                        val index = player.currentMediaItemIndex
                        if (index > 0)
                            player.removeMediaItems(0, index - 1)
                        if (index < player.mediaItemCount - 1)
                            player.removeMediaItems(index + 1, player.mediaItemCount - 1)
                        queueManager.updateList { items ->
                            items.toMutableList().filterIndexed { i, _ -> i == index }
                        }
                        queueManager.updateIndex(player.currentMediaItemIndex)
                    }

                    is Swap -> {
                        player.moveMediaItem(event.from, event.to)
                        val index = player.currentMediaItemIndex
                        queueManager.updateList {
                            it.toMutableList().apply {
                                add(event.to, removeAt(event.from))
                            }
                        }
                        queueManager.updateIndex(index)
                    }

                    ToggleShuffle -> {
                        player.shuffleModeEnabled = !player.shuffleModeEnabled
                    }

                    is AddToQueue -> {
                        queueManager
                            .addToQueue(event.items, event.index)
                            .map { it.toMediaItem() }.let { mediaItems ->
                                event.index?.let { index ->
                                    player.addMediaItems(index, mediaItems)
                                } ?: player.addMediaItems(mediaItems)
                            }
                        queueManager.updateIndex(player.currentMediaItemIndex)
                        player.prepare()
                    }

                    is PlayNext -> {
                        onEvent(AddToQueue(event.items, _queueIndex.first() + 1))
                        onEvent(Resume)
                    }

                    else -> {}
                }
            }
    }

    override suspend fun getPlayerState(): StateFlow<PlayerState> = stateManager.playerState
}
