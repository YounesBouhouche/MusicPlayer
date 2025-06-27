package younesbouhouche.musicplayer.core.domain.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.glance.appwidget.updateAll
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.glance.presentation.MyAppWidget
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.data.models.Queue
import younesbouhouche.musicplayer.main.domain.events.TimerType
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.util.toMediaItems
import younesbouhouche.musicplayer.main.presentation.viewmodel.Task

@OptIn(UnstableApi::class)
class PlayerManager(
    private val context: Context,
    private val dao: AppDao,
    private val dataStore: PlayerDataStore,
    private val stateManager: PlayerStateManager,
    private val queueManager: QueueManager,
    private val playerFactory: PlayerFactory
) {
    private val state = stateManager.playerState

    // DataStore
    private val rememberRepeat = dataStore.rememberRepeat
    private val rememberShuffle = dataStore.rememberShuffle
    private val rememberSpeed = dataStore.rememberSpeed
    private val rememberPitch = dataStore.rememberPitch
    private val repeatMode = dataStore.repeatMode
    private val shuffle = dataStore.shuffle
    private val speed = dataStore.speed
    private val pitch = dataStore.pitch

    /**
     * Get the singleton player instance
     */
    fun getPlayer(): Player? = playerFactory.getPlayerOrNull()
    /**
     * Initialize the player and configure it
     */
    @OptIn(UnstableApi::class)
    fun initialize(scope: CoroutineScope): Player {
        // Get or create the singleton player from factory
        val exoPlayer = playerFactory.getPlayerOrNull() ?: throw IllegalStateException()
        exoPlayer.apply {
            //setSeekParameters(SeekParameters(1000L, 1000L))
            addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        Log.i("PlayerManager", "onPlaybackStateChanged: $playbackState")
                        stateManager.updateState {
                            it.copy(loading = playbackState == Player.STATE_BUFFERING)
                        }
                        if (playbackState == Player.STATE_READY) {
                            stateManager.updateState {
                                it.copy(playState = if (exoPlayer.isPlaying) PlayState.PLAYING else PlayState.PAUSED)
                            }
                            scope.launch {
                                startTimeUpdate()
                            }
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
                            dataStore.override(
                                repeatMode = mode,
                            )
                        }
                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                        scope.launch {
                            stateManager.updateState {
                                it.copy(shuffle = shuffleModeEnabled)
                            }
                            dataStore.override(
                                shuffle = shuffleModeEnabled,
                            )
                        }
                    }

                    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                        super.onPlaybackParametersChanged(playbackParameters)
                        scope.launch(Dispatchers.IO) {
                            stateManager.updateState {
                                it.copy(speed = playbackParameters.speed, pitch = playbackParameters.pitch)
                            }
                            dataStore.override(
                                speed = if (rememberSpeed.first()) playbackParameters.speed else 1f,
                                pitch = if (rememberPitch.first()) playbackParameters.pitch else 1f
                            )
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        stateManager.updateState {
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
                        Timber.tag("PlayerManager")
                            .d("onMediaItemTransition: ${mediaItem?.localConfiguration?.uri}")
                        scope.launch {
                            dao.addTimestamp(mediaItem?.localConfiguration?.uri?.toString() ?: "")
                            queueManager.updateIndex(currentMediaItemIndex)
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
                            } //else {
                            //                            scope.launch {
                            //                                onEvent(PlayerEvent.Stop)
                            //                            }
                            //                        }
                        }
                    }
                },
            )
        }
        return exoPlayer
    }

    val time = MutableStateFlow(0L)
    private var timeTask = Task()

    // Private functions
    suspend fun startTimeUpdate() {
        playerFactory.getPlayerOrNull()?.let { player ->
            timeTask.startRepeating(100L) {
                stateManager.updateState {
                    it.copy(time = player.currentPosition)
                }
            }
        }
    }

    suspend fun seek(index: Int, time: Long) {
        queueManager.updateIndex(index)
        stateManager.updateState {
            it.copy(time = time)
        }
        playerFactory.getPlayerOrNull()?.seekTo(index, time)
    }

    suspend fun play(
        cardsList: List<MusicCard>,
        index: Int = 0,
        time: Long = 0L,
        autoPlay: Boolean = true,
        shuffleMode: Boolean = false
    ) {
        playerFactory.getPlayerOrNull()?.let { player ->
            if (cardsList.isEmpty()) return
            val list = if (shuffleMode) cardsList.shuffled() else cardsList
            queueManager.setQueue(Queue(items = list.map { it.id }, index = index))
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
            queueManager.updateList(cardsList.map { it.id })
            queueManager.updateIndex(index)
            stateManager.suspendUpdateState {
                it.copy(
                    time = time,
                    playState = if (autoPlay) PlayState.PLAYING else PlayState.PAUSED,
                    pitch = pitch.first(),
                )
            }
            startTimeUpdate()
        }
    }

    fun setPlayWhenReady(playWhenReady: Boolean) {
        playerFactory.getPlayerOrNull()?.playWhenReady = playWhenReady
    }
}