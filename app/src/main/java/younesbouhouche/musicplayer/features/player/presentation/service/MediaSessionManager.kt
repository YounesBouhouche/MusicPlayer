package younesbouhouche.musicplayer.features.player.presentation.service

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.updateAll
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import younesbouhouche.musicplayer.core.domain.player.PlayerFactory
import younesbouhouche.musicplayer.core.domain.player.PlayerManager
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.core.domain.repositories.QueueRepository
import younesbouhouche.musicplayer.features.glance.presentation.MyAppWidget
import younesbouhouche.musicplayer.features.player.domain.models.PlayState
import younesbouhouche.musicplayer.features.player.domain.repository.PlayerRepository

class MediaSessionManager(
    private val context: Context,
    private val queueRepository: QueueRepository,
    private val stateManager: PlayerStateManager,
    private val playerManager: PlayerManager,
    private val playerFactory: PlayerFactory,
    private val pendingIntent: PendingIntent,
    private val customCommands: List<CommandButton>
) {
    private var mediaSession: MediaSession? = null
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private lateinit var player: Player
    private val observer =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                if (::player.isInitialized) {
                    stateManager.updateState {
                        it.copy(volume = player.deviceVolume.toFloat())
                    }
                }
            }
        }

    @OptIn(UnstableApi::class)
    suspend fun initialize() {
        Timber.tag("MediaSessionManager").i("Init")
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            observer,
        )
        player = playerFactory.getPlayer()
        val sessionToken =
            SessionToken(context, ComponentName(context, MediaPlayerService::class.java))
        context.startForegroundService(Intent(context, MediaPlayerService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
            Timber.tag("MediaSessionManager")
                .i("Restoring, ${controllerFuture.get().mediaItemCount}")
            // Restore player state
            scope.launch {
                playerFactory.restorePlayerState(controllerFuture.get())
                if (player.playWhenReady) {
                    stateManager.updateState {
                        it.copy(
                            time = player.currentPosition,
                            playState =
                                if (player.isPlaying) PlayState.PLAYING else PlayState.PAUSED,
                            repeatMode = player.repeatMode,
                            shuffle = player.shuffleModeEnabled,
                            speed = player.playbackParameters.speed,
                            pitch = player.playbackParameters.pitch,
                            volume = player.deviceVolume.toFloat(),
                            hasNextItem = player.hasNextMediaItem(),
                            hasPrevItem = player.hasPreviousMediaItem(),
                        )
                    }
                    queueRepository.setCurrentIndex(player.currentMediaItemIndex)
                    withContext(Dispatchers.Main) {
                        MyAppWidget().updateAll(context)
                    }
                }
                playerManager.initialize()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    @OptIn(UnstableApi::class)
    fun createSession(context: Context, player: Player) {
        MediaSession.Builder(context, player)
            .setId("MusicPlayerMediaPlayerService")
            .setSessionActivity(pendingIntent)
            .setCustomLayout(customCommands)
            .build().also {
                mediaSession = it
            }
    }

    fun getSession(): MediaSession? = mediaSession

    /**
     * Set the media session directly
     */
    fun setSession(session: MediaSession) {
        // Release any existing session first
        //mediaSession?.release()
        mediaSession = session

    }

    fun release() {
        // Just release the media session but not the player
        // The player will be released by the PlayerFactory
        mediaSession?.release()
        mediaSession = null
    }

    fun updateCustomLayout(layout: List<CommandButton>) {
        mediaSession?.setCustomLayout(layout)
    }
}