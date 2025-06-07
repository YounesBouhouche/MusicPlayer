package younesbouhouche.musicplayer.core.domain

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_SET_REPEAT_MODE
import androidx.media3.common.Player.COMMAND_SET_SHUFFLE_MODE
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.domain.repo.FilesRepo

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
    private val repo by inject<FilesRepo>()

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
                // Initialize custom notification provider first
                customMediaNotificationProvider = CustomMediaNotificationProvider(this@MediaPlayerService)
                customMediaNotificationProvider.setSmallIcon(R.drawable.media3_notification_small_icon)

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

                    // Create media session with the custom commands
                    mediaSession =
                        MediaSession
                            .Builder(this@MediaPlayerService, this)
                            .setId("MusicPlayerMediaPlayerService")
                            .setSessionActivity(pendingIntent)
                            .setCustomLayout(notificationCustomCmdButtons)
                            .build()

                    // Register available commands for the custom buttons
                    player?.addListener(object : Player.Listener {
                        override fun onAvailableCommandsChanged(commands: Player.Commands) {
                            mediaSession?.setCustomLayout(notificationCustomCmdButtons)
                        }
                    })
                }

                // Set the notification provider after the session is created
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
            CUSTOM_COMMAND_REWIND_10S_ACTION_ID -> {
                // Seek back 10 seconds
                val currentPosition = session.player.currentPosition
                val newPosition = (currentPosition - 10_000).coerceAtLeast(0)
                session.player.seekTo(newPosition)
            }
            CUSTOM_COMMAND_FORWARD_10S_ACTION_ID -> {
                // Seek forward 10 seconds
                val currentPosition = session.player.currentPosition
                val duration = session.player.duration
                val newPosition = (currentPosition + 10_000).coerceAtMost(if (duration > 0) duration else currentPosition)
                session.player.seekTo(newPosition)
            }
            CUSTOM_COMMAND_LOOP_ACTION_ID -> {
                // Toggle repeat mode
                val currentRepeatMode = session.player.repeatMode
                val newRepeatMode = when (currentRepeatMode) {
                    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
                    Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
                    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF
                    else -> Player.REPEAT_MODE_OFF
                }
                session.player.repeatMode = newRepeatMode
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
    ): ConnectionResult {
        // Create session commands for all custom buttons
        val customSessionCommands = ImmutableList.builder<SessionCommand>().apply {
            NotificationCustomCmdButton.entries.forEach { button ->
                button.commandButton.sessionCommand?.let { cmd ->
                    add(cmd)
                }
            }
        }.build()

        if (session.isMediaNotificationController(controller)) {
            val sessionCommands = ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .addSessionCommands(customSessionCommands)
                .build()

            val playerCommands = ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                .add(COMMAND_SET_REPEAT_MODE)
                .add(COMMAND_SET_SHUFFLE_MODE)
                .build()

            return AcceptedResultBuilder(session)
                .setMediaButtonPreferences(notificationCustomCmdButtons)
                .setAvailablePlayerCommands(playerCommands)
                .setAvailableSessionCommands(sessionCommands)
                .build()
        }

        return AcceptedResultBuilder(session)
            .setAvailableSessionCommands(
                ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                    .addSessionCommands(customSessionCommands)
                    .build()
            )
            .build()
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
    ): ListenableFuture<MediaItemsWithStartPosition> {
        val settable = SettableFuture.create<MediaItemsWithStartPosition>()
        CoroutineScope(Dispatchers.Main).launch {
            val resumptionPlaylist = MediaItemsWithStartPosition(
                repo.getQueue().first().map {
                    it.toMediaItem()
                },
                repo.getIndex().first(),
                repo.getState().first().time,
            )
            settable.set(resumptionPlaylist)
        }
        return settable
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

