package younesbouhouche.musicplayer.core.domain

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.settings.data.SettingsDataStore

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MediaPlayerService : MediaSessionService(), MediaSession.Callback {
    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var controller: MediaSession.ControllerInfo? = null
    private lateinit var customMediaNotificationProvider: CustomMediaNotificationProvider
    private val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            30_000,  // Min buffer before playback starts (30s)
            120_000, // Max buffer (120s)
            15_000,  // Play when buffer reaches (15s)
            5_000    // Rebuffer threshold (5s)
        )
        .build()

    private val notificationCustomCmdButtons =
        NotificationCustomCmdButton.entries.map { command -> command.commandButton }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onCreate() {
        super.onCreate()
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                packageManager.getLaunchIntentForPackage(packageName),
                FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT,
            )
        CoroutineScope(Dispatchers.IO).launch {
            val skipSilence = PlayerDataStore(this@MediaPlayerService).skipSilence.first()
            withContext(Dispatchers.Main) {
                with(
                    ExoPlayer
                        .Builder(this@MediaPlayerService)
                        .setLoadControl(loadControl)
                        .setHandleAudioBecomingNoisy(true)
                        .setSkipSilenceEnabled(skipSilence)
                        .setAudioAttributes(
                            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).build(),
                            true,
                        )
                        .build(),
                ) {
                    setSeekParameters(SeekParameters(1000L, 1000L))
                    player = this
                    mediaSession =
                        MediaSession
                            .Builder(this@MediaPlayerService, this)
                            .setId("MusicPlayerMediaPlayerService")
                            .setSessionActivity(pendingIntent)
                            .setCustomLayout(notificationCustomCmdButtons)
                            .build()
                }
                customMediaNotificationProvider = CustomMediaNotificationProvider(this@MediaPlayerService)
                setMediaNotificationProvider(customMediaNotificationProvider)
            }
        }
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle,
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            NotificationCustomCmdButton.REWIND.customAction -> {
                session.player.seekBack()
            }
        }
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        if (!player.playWhenReady || player.mediaItemCount == 0) stopSelf()
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        this.controller = controller
        this.mediaSession = session
        val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
        notificationCustomCmdButtons.forEach {
            it.sessionCommand?.let(availableSessionCommands::add)
        }
        return MediaSession.ConnectionResult.accept(
            availableSessionCommands.build(),
            connectionResult.availablePlayerCommands,
        )
    }

    override fun onPostConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
    ) {
        super.onPostConnect(session, controller)
        if (notificationCustomCmdButtons.isNotEmpty()) {
            mediaSession!!.setCustomLayout(notificationCustomCmdButtons)
        }
    }

    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        this.controller = controller
        this.mediaSession = mediaSession
        return super.onPlaybackResumption(mediaSession, controller)
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
    ): ListenableFuture<MutableList<MediaItem>> {
        this.controller = controller
        this.mediaSession = mediaSession
        val updatedItems = mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
        return Futures.immediateFuture(updatedItems)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
