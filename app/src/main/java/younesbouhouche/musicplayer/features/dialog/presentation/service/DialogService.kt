package younesbouhouche.musicplayer.features.dialog.presentation.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import younesbouhouche.musicplayer.features.player.presentation.service.CustomMediaNotificationProvider
import younesbouhouche.musicplayer.features.player.presentation.service.NotificationCustomCmdButton

@OptIn(UnstableApi::class)
class DialogService : MediaSessionService(), MediaSession.Callback {
    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var controller: MediaSession.ControllerInfo? = null
    private lateinit var customMediaNotificationProvider: CustomMediaNotificationProvider

    private val notificationCustomCmdButtons =
        NotificationCustomCmdButton.entries.map { command -> command.commandButton }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onCreate() {
        super.onCreate()
        customMediaNotificationProvider = CustomMediaNotificationProvider(this)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                packageManager.getLaunchIntentForPackage(packageName),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        with(
            ExoPlayer
                .Builder(this)
                .setHandleAudioBecomingNoisy(true)
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
                    .Builder(
                        this@DialogService,
                        this,
                    )
                    .setId("MusicPlayerDialogService")
                    .setSessionActivity(pendingIntent)
                    .setCustomLayout(notificationCustomCmdButtons)
                    .build()
        }
        setMediaNotificationProvider(customMediaNotificationProvider)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (mediaSession == null) {
            customMediaNotificationProvider.ensureForeground(this)
        }
        return super.onStartCommand(intent, flags, startId)
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
            NotificationCustomCmdButton.FORWARD.customAction -> {
                session.player.seekForward()
            }
            NotificationCustomCmdButton.REWIND_10S.customAction -> {
                // Seek back 10 seconds
                val currentPosition = session.player.currentPosition
                val newPosition = (currentPosition - 10_000).coerceAtLeast(0)
                session.player.seekTo(newPosition)
            }
            NotificationCustomCmdButton.FORWARD_10S.customAction -> {
                // Seek forward 10 seconds
                val currentPosition = session.player.currentPosition
                val newPosition = (currentPosition + 10_000).coerceAtMost(session.player.duration)
                session.player.seekTo(newPosition)
            }
            NotificationCustomCmdButton.LOOP.customAction -> {
                // Toggle loop mode
                session.player.repeatMode =
                    if (session.player.repeatMode == ExoPlayer.REPEAT_MODE_OFF) {
                        ExoPlayer.REPEAT_MODE_ALL
                    } else {
                        ExoPlayer.REPEAT_MODE_OFF
                    }
            }
            else -> return Futures.immediateFuture(SessionResult(SessionError.ERROR_UNKNOWN))
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
        isForPlayback: Boolean
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        this.controller = controller
        this.mediaSession = mediaSession
        return super.onPlaybackResumption(mediaSession, controller, isForPlayback)
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