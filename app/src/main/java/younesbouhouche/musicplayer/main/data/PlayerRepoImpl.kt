package younesbouhouche.musicplayer.main.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.updateAll
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.MediaPlayerService
import younesbouhouche.musicplayer.core.domain.models.ItemData
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.glance.presentation.MyAppWidget
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.data.events.PlayerEvent
import younesbouhouche.musicplayer.main.data.models.Queue
import younesbouhouche.musicplayer.main.data.util.getVolume
import younesbouhouche.musicplayer.main.domain.events.TimerType
import younesbouhouche.musicplayer.main.domain.repo.PlayerRepo
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.util.toMediaItems
import younesbouhouche.musicplayer.main.presentation.viewmodel.Task
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.roundToInt

class PlayerRepoImpl(
    val context: Context,
    val audioManager: AudioManager,
    val dao: AppDao,
    val playerDataStore: PlayerDataStore
): PlayerRepo(MutableStateFlow(PlayerState(volume = audioManager.getVolume()))) {
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private lateinit var player: Player
    private val observer =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                playerState.update {
                    it.copy(volume = audioManager.getVolume())
                }
            }
        }
    private val _queue = dao.getQueue().map { it ?: Queue() }
    private val _queueList = _queue.map { it.items }
    private val _queueIndex = _queue.map { it.index }
    val time = MutableStateFlow<Long>(0L)
    private var timerTask = Task()
    private var timeTask = Task()

    private val rememberRepeat = playerDataStore.rememberRepeat
    private val rememberShuffle = playerDataStore.rememberShuffle
    private val rememberSpeed = playerDataStore.rememberSpeed
    private val rememberPitch = playerDataStore.rememberPitch
    private val repeatMode = playerDataStore.repeatMode
    private val shuffle = playerDataStore.shuffle
    private val speed = playerDataStore.speed
    private val pitch = playerDataStore.pitch

    private suspend fun startTimeUpdate() =
        timerTask.startRepeating(100L) {
            playerState.update {
                it.copy(time = player.currentPosition)
            }
        }

    suspend fun play(
        cardsList: List<MusicCard>,
        index: Int = 0,
        time: Long = 0L,
        autoPlay: Boolean = true,
        shuffleMode: Boolean = false
    ) {
        if (cardsList.isEmpty()) return
        val list = if (shuffleMode == true) cardsList.shuffled() else cardsList
        if ((_queueList.first() == list) and (playerState.value.playState != PlayState.STOP)) {
            if (index != _queueIndex.first()) seek(index, time)
            return
        }
        dao.upsertQueue(Queue(items = list.map { it.id }, index = index))
        player.setMediaItems(list.toMediaItems())
        player.seekTo(index, time)
        player.prepare()
        if (autoPlay) player.play()
        player.repeatMode =
            if (rememberRepeat.first()) repeatMode.first()
            else Player.REPEAT_MODE_OFF
        if (rememberShuffle.first())
            player.shuffleModeEnabled = shuffle.first()
        player.playbackParameters =
            PlaybackParameters(
                if (rememberSpeed.first()) speed.first() else 1f,
                if (rememberPitch.first()) pitch.first() else 1f
            )
        list.getOrNull(index)?.let {
            dao.addTimestamp(it.path)
        }
        dao.updateCurrentIndex(index)
        playerState.update {
            it.copy(
                time = time,
                playState = if (autoPlay) PlayState.PLAYING else PlayState.PAUSED,
                pitch = pitch.first(),
            )
        }
        startTimeUpdate()
    }

    private suspend fun seek(
        index: Int,
        time: Long,
    ) {
        dao.updateCurrentIndex(index)
        playerState.update {
            it.copy(time = time)
        }
        player.seekTo(index, time)
    }

    override suspend fun onPlayerEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.Backward -> player.seekTo(player.currentPosition - event.ms)
            is PlayerEvent.Forward -> player.seekTo(player.currentPosition + event.ms)
            PlayerEvent.Next -> player.seekToNext()
            PlayerEvent.Pause -> player.pause()
            PlayerEvent.PauseResume -> {
                if (this::player.isInitialized) {
                    if (player.isPlaying) player.pause()
                    else player.play()
                }
            }

            is PlayerEvent.Play -> play(event.items, event.index, shuffleMode = event.shuffle)

            PlayerEvent.Previous -> player.seekToPrevious()

            PlayerEvent.Resume -> player.play()

            is PlayerEvent.Seek ->
                if (!((event.skipIfSameIndex) and (event.index == _queueIndex.first()))) {
                    player.seekTo(event.index, event.time)
                }

            is PlayerEvent.SeekTime -> player.seekTo(event.time)

            PlayerEvent.Stop -> {
                player.stop()
                player.clearMediaItems()
                dao.upsertQueue(Queue())
                playerState.update {
                    it.copy(
                        playState = PlayState.STOP,
                        timer = TimerType.Disabled,
                    )
                }
            }

            is PlayerEvent.Remove -> {
                player.removeMediaItem(event.index)
                dao.updateQueue(
                    _queueList.first().toMutableList().apply {
                        removeAt(event.index)
                    },
                )
            }

            is PlayerEvent.Swap -> {
                player.moveMediaItem(event.from, event.to)
                val index = player.currentMediaItemIndex
                dao.upsertQueue(
                    Queue(
                        items =
                        _queueList.first().toMutableList().apply {
                            add(event.to, removeAt(event.from))
                        },
                        index = index,
                    ),
                )
            }

            PlayerEvent.CycleRepeatMode -> {
                onPlayerEvent(
                    PlayerEvent.SetRepeatMode(
                        when (player.repeatMode) {
                            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                            else -> Player.REPEAT_MODE_OFF
                        },
                    ),
                )
            }

            is PlayerEvent.SetRepeatMode -> {
                player.repeatMode = event.repeatMode
            }

            PlayerEvent.ToggleShuffle -> {
                player.shuffleModeEnabled = !player.shuffleModeEnabled
            }

            PlayerEvent.ResetSpeed -> player.setPlaybackSpeed(1f)
            is PlayerEvent.SetSpeed -> player.setPlaybackSpeed(event.speed)
            is PlayerEvent.SetTimer -> {
                event.timer.let {
                    playerState.update { state ->
                        state.copy(timer = it)
                    }
                    if (it is TimerType.Disabled) {
                        timerTask.stop()
                    } else {
                        timerTask.start {
                            when (it) {
                                is TimerType.Duration -> {
                                    while ((playerState.value.timer as TimerType.Duration).ms > 0) {
                                        delay(1000L)
                                        playerState.update { state ->
                                            state.copy(
                                                timer =
                                                TimerType.Duration(
                                                    (state.timer as TimerType.Duration).ms - 1000L,
                                                ),
                                            )
                                        }
                                    }
                                    onPlayerEvent(PlayerEvent.Stop)
                                }

                                is TimerType.Time -> {
                                    timerTask.startRepeating(1000L) {
                                        val time = with(LocalDateTime.now()) { hour * 60 + minute }
                                        if (abs(time - (it.hour * 60 + it.min)) == 0) timerTask.stop()
                                    }
                                    onPlayerEvent(PlayerEvent.Stop)
                                }

                                else -> return@start
                            }
                        }
                    }
                }
            }

            is PlayerEvent.UpdateFavorite -> {
                dao.upsertItem(ItemData(event.path, event.favorite))
            }

            is PlayerEvent.PlayNext -> {
                if (playerState.value.playState == PlayState.STOP) {
                    play(event.items, autoPlay = false)
                    return
                }
                event.items
                    .map { _queueList.first().indexOf(it.id) }
                    .filter { it >= 0 }
                    .forEach { player.removeMediaItem(it) }
                player.addMediaItems(
                    player.currentMediaItemIndex + 1,
                    event.items.toMediaItems(),
                )
                player.prepare()
                player.play()
                val items = event.items.map { it.id }
                dao.upsertQueue(
                    Queue(
                        items =
                            _queueList.first().toMutableList().apply {
                                removeAll(items)
                                addAll(player.currentMediaItemIndex + 1, items)
                            },
                        index = player.currentMediaItemIndex,
                    ),
                )
            }

            is PlayerEvent.AddToQueue -> {
                if (playerState.value.playState == PlayState.STOP) {
                    play(event.items, autoPlay = false)
                    return
                }
                val list =
                    event.items.filter { item ->
                        !_queueList.first().any { it == item.id }
                    }
                dao.updateQueue(
                    _queueList.first().toMutableList().apply { addAll(list.map { it.id }) },
                )
                player.addMediaItems(list.toMediaItems())
            }

            PlayerEvent.DecreaseVolume -> {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER,
                    0,
                )
                playerState.update {
                    it.copy(volume = audioManager.getVolume())
                }
            }

            PlayerEvent.IncreaseVolume -> {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    0,
                )
                playerState.update {
                    it.copy(volume = audioManager.getVolume())
                }
            }

            is PlayerEvent.SetVolume -> {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    (event.volume * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                        .roundToInt(),
                    0,
                )
                playerState.update {
                    it.copy(volume = audioManager.getVolume())
                }
            }

            is PlayerEvent.SetPitch ->
                event.pitch.let {
                    player.playbackParameters =
                        PlaybackParameters(
                            player.playbackParameters.speed,
                            it,
                        )
                    playerState.update { state ->
                        state.copy(pitch = it)
                    }
                    playerDataStore.override(pitch = it)
                }

            is PlayerEvent.SetPlayerVolume -> {
                if (playerState.value.playState != PlayState.STOP) {
                    player.volume = event.volume
                }
            }
        }
    }

    override fun init(scope: CoroutineScope, callback: () -> Unit) {
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            observer,
        )
        val sessionToken =
            SessionToken(context, ComponentName(context, MediaPlayerService::class.java))
        context.startForegroundService(Intent(context, MediaSessionService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            player = controllerFuture.get()
            if (player.playWhenReady) {
                playerState.update {
                    it.copy(
                        time = player.currentPosition,
                        playState =
                        if (player.isPlaying) PlayState.PLAYING else PlayState.PAUSED,
                        repeatMode = player.repeatMode,
                        shuffle = player.shuffleModeEnabled,
                        speed = player.playbackParameters.speed,
                        pitch = player.playbackParameters.pitch,
                    )
                }
                scope.launch {
                    dao.updateCurrentIndex(player.currentMediaItemIndex)
                    MyAppWidget().updateAll(context)
                }
            }
            scope.launch {
                timeTask.startRepeating(100L) {
                    playerState.update {
                        it.copy(time = player.currentPosition)
                    }
                }
            }
            player.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        playerState.update {
                            it.copy(loading = playbackState == Player.STATE_BUFFERING)
                        }
                    }

                    override fun onRepeatModeChanged(mode: Int) {
                        super.onRepeatModeChanged(mode)
                        scope.launch {
                            playerState.update {
                                it.copy(
                                    repeatMode = mode,
                                    hasNextItem = player.hasNextMediaItem(),
                                    hasPrevItem = player.hasPreviousMediaItem(),
                                )
                            }
                            playerDataStore.override(
                                repeatMode = mode,
                            )
                        }
                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                        scope.launch {
                            playerState.update {
                                it.copy(shuffle = shuffleModeEnabled)
                            }
                            playerDataStore.override(
                                shuffle = shuffleModeEnabled,
                            )
                        }
                    }

                    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                        super.onPlaybackParametersChanged(playbackParameters)
                        scope.launch(Dispatchers.IO) {
                            playerState.update {
                                it.copy(speed = playbackParameters.speed)
                            }
                            playerDataStore.override(
                                speed = if (rememberSpeed.first()) playbackParameters.speed else 1f,
                                pitch = if (rememberPitch.first()) playbackParameters.pitch else 1f,
                            )
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        playerState.update {
                            it.copy(
                                playState = if (isPlaying) PlayState.PLAYING else PlayState.PAUSED
                            )
                        }
                        scope.launch {
                            MyAppWidget().updateAll(context)
                        }
                    }

                    override fun onMediaItemTransition(
                        mediaItem: MediaItem?,
                        reason: Int
                    ) {
                        super.onMediaItemTransition(mediaItem, reason)
                        scope.launch {
                            dao.updateCurrentIndex(player.currentMediaItemIndex)
                            MyAppWidget().updateAll(context)
//                            try {
//                                dao.addTimestamp(player.value[player.currentMediaItemIndex].path)
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
                        }
                        playerState.update {
                            it.copy(
                                hasNextItem = player.hasNextMediaItem(),
                                hasPrevItem = player.hasPreviousMediaItem(),
                            )
                        }
                        if (
                            (playerState.value.timer is TimerType.End) and
                            (
                                    (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) or
                                            (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
                                    )
                        ) {
                            if ((playerState.value.timer as TimerType.End).tracks > 1) {
                                playerState.update {
                                    val timer = (it.timer as TimerType.End)
                                    it.copy(timer = timer.copy(tracks = timer.tracks - 1))
                                }
                            } else {
                                scope.launch {
                                    onPlayerEvent(PlayerEvent.Stop)
                                }
                            }
                        }
                    }
                },
            )
            callback()
        }, ContextCompat.getMainExecutor(context))
    }

    override fun finalize() {
        context.contentResolver.unregisterContentObserver(observer)
    }
}