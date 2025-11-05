package younesbouhouche.musicplayer.core.domain

import android.content.Intent
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_SET_REPEAT_MODE
import androidx.media3.common.Player.COMMAND_SET_SHUFFLE_MODE
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.commands.CustomCommandHandler
import younesbouhouche.musicplayer.core.domain.player.PlayerFactory
import younesbouhouche.musicplayer.core.domain.player.PlayerManager
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.core.domain.player.QueueManager
import younesbouhouche.musicplayer.core.domain.session.MediaSessionManager
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.main.presentation.states.PlayState

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MediaPlayerService : MediaSessionService(), MediaSession.Callback {
    private val playerFactory by inject<PlayerFactory>()
    private val playerManager by inject<PlayerManager>()
    private val stateManager by inject<PlayerStateManager>()
    private val sessionManager by inject<MediaSessionManager>()
    private val queueManager by inject<QueueManager>()
    private val mediaRepository by inject<MediaRepository>()
    private lateinit var commandHandler: CustomCommandHandler
    private lateinit var notificationProvider: CustomMediaNotificationProvider
    val notificationCustomCmdButtons =
        NotificationCustomCmdButton.entries.map { command -> command.commandButton }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        sessionManager.getSession()

    override fun onCreate() {
        super.onCreate()

        notificationProvider = CustomMediaNotificationProvider(this)
        notificationProvider.setSmallIcon(R.drawable.media3_notification_small_icon)

        serviceScope.launch {
            withContext(Dispatchers.Main) {
                val player = playerManager.initialize(serviceScope)
                commandHandler = CustomCommandHandler(player)
                player.addListener(object : Player.Listener {
                    override fun onAvailableCommandsChanged(commands: Player.Commands) {
                        sessionManager.updateCustomLayout(notificationCustomCmdButtons)
                    }
                })
                sessionManager.createSession(this@MediaPlayerService, player)
                setMediaNotificationProvider(notificationProvider)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (sessionManager.getSession() == null) {
            notificationProvider.ensureForeground(this)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle,
    ): ListenableFuture<SessionResult> {
        commandHandler.handleCommand(customCommand.customAction)
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        playerFactory.getPlayerOrNull().let { player ->
            if (!player.playWhenReady || player.mediaItemCount == 0) stopSelf()
        }
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): ConnectionResult {
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
            sessionManager.updateCustomLayout(notificationCustomCmdButtons)
        }
    }

    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): ListenableFuture<MediaItemsWithStartPosition> {
        val settable = SettableFuture.create<MediaItemsWithStartPosition>()
        serviceScope.launch {
            try {
                val queue = queueManager.getQueue().first()
                if (queue == null || queue.items.isEmpty()) {
                    settable.set(MediaItemsWithStartPosition(emptyList(), 0, 0))
                    return@launch
                }
                val currentIndex = queueManager.getCurrentIndex() ?: 0
                val position = stateManager.playerState.value.time
                val isPlaying = stateManager.playerState.value.playState == PlayState.PLAYING
                val mediaItems = queue.items.mapNotNull {
                    mediaRepository.getUriById(it)?.let { uri -> MediaItem.fromUri(uri) }
                }
                val resumptionPlaylist = MediaItemsWithStartPosition(
                    mediaItems,
                    currentIndex,
                    position
                )

                settable.set(resumptionPlaylist)
                withContext(Dispatchers.Main) {
                    playerManager.setPlayWhenReady(isPlaying)
                }
            } catch (_: Exception) {
                settable.set(MediaItemsWithStartPosition(emptyList(), 0, 0))
            }
        }
        return settable
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
    ): ListenableFuture<MutableList<MediaItem>> {
        sessionManager.setSession(mediaSession)
        val updatedItems = mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
        return Futures.immediateFuture(updatedItems)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        sessionManager.release()
        playerFactory.releasePlayer()
        super.onDestroy()
    }
}