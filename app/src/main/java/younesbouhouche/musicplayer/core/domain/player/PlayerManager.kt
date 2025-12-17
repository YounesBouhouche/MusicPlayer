package younesbouhouche.musicplayer.core.domain.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.glance.appwidget.updateAll
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository
import younesbouhouche.musicplayer.core.domain.repositories.QueueRepository
import younesbouhouche.musicplayer.features.glance.presentation.MyAppWidget
import younesbouhouche.musicplayer.features.main.domain.events.TimerType
import younesbouhouche.musicplayer.features.main.presentation.util.toMediaItems
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.Task
import younesbouhouche.musicplayer.features.player.domain.models.PlayState

@OptIn(UnstableApi::class)
class PlayerManager(
    private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val stateManager: PlayerStateManager,
    private val musicRepository: MusicRepository,
    private val queueRepository: QueueRepository,
    private val playerFactory: PlayerFactory,
    private val scope: CoroutineScope,
) {
    val state = stateManager.playerState

    private val rememberRepeat = preferencesRepository.get(SettingsPreference.RememberRepeat)
    private val rememberShuffle = preferencesRepository.get(SettingsPreference.RememberShuffle)
    private val rememberSpeed = preferencesRepository.get(SettingsPreference.RememberSpeed)
    private val rememberPitch = preferencesRepository.get(SettingsPreference.RememberPitch)
    private val repeatMode = preferencesRepository.get(SettingsPreference.RepeatMode)
    private val shuffle = preferencesRepository.get(SettingsPreference.ShuffleMode)
    private val speed = preferencesRepository.get(SettingsPreference.Speed)
    private val pitch = preferencesRepository.get(SettingsPreference.Pitch)

    private var initialized = false

    @OptIn(UnstableApi::class)
    suspend fun initialize(): Player {
        val exoPlayer = playerFactory.getPlayer()
        if (initialized) return exoPlayer
        exoPlayer.apply {
            addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        stateManager.updateState {
                            it.copy(loading = playbackState == Player.STATE_BUFFERING)
                        }
                        when(playbackState) {
                            Player.STATE_READY -> {
                                stateManager.updateState {
                                    it.copy(playState = if (exoPlayer.isPlaying) PlayState.PLAYING else PlayState.PAUSED)
                                }
                                scope.launch {
                                    startTimeUpdate()
                                }
                            }
                            Player.STATE_IDLE -> {
                                stateManager.updateState {
                                    it.copy(playState = PlayState.STOP)
                                }
                            }
                            else -> Unit
                        }
                    }

                    override fun onRepeatModeChanged(mode: Int) {
                        super.onRepeatModeChanged(mode)
                        scope.launch {
                            stateManager.updateState {
                                it.copy(
                                    repeatMode = mode,
                                    hasNextItem = hasNextMediaItem(),
                                    hasPrevItem = hasPreviousMediaItem(),
                                )
                            }
                            preferencesRepository.set(SettingsPreference.RepeatMode, mode)
                        }
                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                        scope.launch {
                            stateManager.updateState {
                                it.copy(shuffle = shuffleModeEnabled)
                            }
                            preferencesRepository.set(SettingsPreference.ShuffleMode, shuffleModeEnabled)
                        }
                    }

                    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                        super.onPlaybackParametersChanged(playbackParameters)
                        scope.launch(Dispatchers.IO) {
                            stateManager.updateState {
                                it.copy(
                                    speed = playbackParameters.speed,
                                    pitch = playbackParameters.pitch
                                )
                            }
                            preferencesRepository.set(
                                SettingsPreference.Speed,
                                if (rememberSpeed.first()) playbackParameters.speed else 1f
                            )
                            preferencesRepository.set(
                                SettingsPreference.Pitch,
                                if (rememberPitch.first()) playbackParameters.pitch else 1f
                            )
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        stateManager.updateState {
                            it.copy(
                                playState = when {
                                    exoPlayer.playbackState == Player.STATE_IDLE -> PlayState.STOP
                                    isPlaying -> PlayState.PLAYING
                                    else -> PlayState.PAUSED
                                }
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
                        Timber.tag("PlayerManager")
                            .d("onMediaItemTransition: ${mediaItem?.localConfiguration?.uri}")
                        scope.launch {
                            queueRepository.setCurrentIndex(currentMediaItemIndex)
                            (mediaItem?.localConfiguration?.tag as? Long)?.let {
                                musicRepository.addSongToPlayHistory(it)
                            }
                            MyAppWidget().updateAll(context)
                        }
                        stateManager.updateState {
                            it.copy(
                                hasNextItem = hasNextMediaItem(),
                                hasPrevItem = hasPreviousMediaItem(),
                            )
                        }
                        if (
                            (state.value.timer is TimerType.End) and
                            (
                                    (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) or
                                            (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
                                    )
                        ) {
                            if ((state.value.timer as TimerType.End).tracks > 1) {
                                stateManager.updateState {
                                    val timer = (it.timer as TimerType.End)
                                    it.copy(timer = timer.copy(tracks = timer.tracks - 1))
                                }
                            } else {
                                stop()
                            }
                        }
                    }
                },
            )
        }
        initialized = true
        return exoPlayer
    }

    private var timeTask = Task()
    private var timerTask = Task()
    private var lastSeekTime = 0L
    private val seekLockDuration = 100L // milliseconds

    // Private functions
    suspend fun startTimeUpdate() {
        playerFactory.getPlayer().let { player ->
            timeTask.startRepeating(100L) {
                stateManager.updateState {
                    it.copy(time = player.currentPosition)
                }
            }
        }
    }

    suspend fun handleTimer() {
        if (stateManager.playerState.value.timer is TimerType.Disabled) {
            timerTask.stop()
            return
        }
        timerTask.startRepeating(1000L) {
            val timer = stateManager.playerState.value.timer
            if (timer is TimerType.Duration) {
                if (timer.ms <= 1000L) {
                    stop()
                } else {
                    stateManager.updateState { it.copy(timer = timer.copy(ms = timer.ms - 1000L)) }
                }
            } else if (timer is TimerType.Time) {
                val currentTime = System.currentTimeMillis()
                val targetTime = timer.getTargetDateMillis()
                if (currentTime >= targetTime) {
                    stop()
                }
            }
        }
    }

    suspend fun seek(index: Int?, time: Long, skipIfSameIndex: Boolean = true) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSeekTime < seekLockDuration) {
            return
        }
        if (skipIfSameIndex and (index == playerFactory.getPlayer().currentMediaItemIndex)) {
            return
        }
        lastSeekTime = currentTime
        if (index != null) {
            queueRepository.setCurrentIndex(index)
            playerFactory.getPlayer().seekTo(index, time)
        } else
            playerFactory.getPlayer().seekTo(time)
        stateManager.updateState {
            it.copy(time = time)
        }
        MyAppWidget().updateAll(context)
    }

    suspend fun play(
        tracks: List<Long>,
        index: Int = 0,
        time: Long = 0L,
        autoPlay: Boolean = true,
        shuffleMode: Boolean = false
    ) {
        println("PlayerManager.play: tracks=$tracks, index=$index, time=$time, autoPlay=$autoPlay, shuffleMode=$shuffleMode")
        playerFactory.getPlayer().let { player ->
            if (tracks.isEmpty()) return
            val songs = withContext(Dispatchers.IO) {
                musicRepository.getSongs(tracks)
            }
            val list = if (shuffleMode) songs.shuffled() else songs
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
                musicRepository.addSongToPlayHistory(it.id)
            }
            queueRepository.createQueue(tracks)
            queueRepository.setCurrentIndex(index)
            stateManager.suspendUpdateState {
                it.copy(
                    time = time,
                    playState = if (autoPlay) PlayState.PLAYING else PlayState.PAUSED,
                    pitch = pitch.first(),
                    speed = speed.first(),
                )
            }
            startTimeUpdate()
        }
    }

    suspend fun stop() {
        playerFactory.getPlayer().let {
            it.stop()
            it.clearMediaItems()
        }
        timeTask.stop()
        timerTask.stop()
        stateManager.updateState {
            it.copy(
                playState = PlayState.STOP,
                time = 0L,
                timer = TimerType.Disabled
            )
        }
        withContext(Dispatchers.IO) {
            queueRepository.clearQueue()
        }
    }

    suspend fun setPlayWhenReady(playWhenReady: Boolean) {
        playerFactory.getPlayer().playWhenReady = playWhenReady
    }
}