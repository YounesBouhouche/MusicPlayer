package younesbouhouche.musicplayer.core.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.collect.ImmutableList
import younesbouhouche.musicplayer.R

@UnstableApi
class CustomMediaNotificationProvider(context: Context) : DefaultMediaNotificationProvider(context) {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_playback_channel"
    }

    init {
        createNotificationChannel(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows currently playing music"
                setShowBadge(false)
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun ensureForeground(service: MediaSessionService) {
        val notification = createPlaceholderNotification(service)
        service.startForeground(NOTIFICATION_ID, notification)
    }

    private fun createPlaceholderNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Loading...")
            .setSmallIcon(R.drawable.media3_notification_small_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    override fun getMediaButtons(
        session: MediaSession,
        playerCommands: Player.Commands,
        mediaButtonPreferences: ImmutableList<CommandButton>,
        showPauseButton: Boolean
    ): ImmutableList<CommandButton> {
        val playPauseButton = mediaButtonPreferences.find { button ->
            button.sessionCommand == null &&
            (button.iconResId == androidx.media3.session.R.drawable.media3_icon_play ||
             button.iconResId == androidx.media3.session.R.drawable.media3_icon_pause)
        }

        val previousButton = mediaButtonPreferences.find { button ->
            button.sessionCommand == null &&
            button.iconResId == androidx.media3.session.R.drawable.media3_icon_previous
        }

        val nextButton = mediaButtonPreferences.find { button ->
            button.sessionCommand == null &&
            button.iconResId == androidx.media3.session.R.drawable.media3_icon_next
        }

        return ImmutableList.builder<CommandButton>().apply {
            if (previousButton != null) add(previousButton)
            if (playPauseButton != null) add(playPauseButton)
            if (nextButton != null) add(nextButton)
            add(NotificationCustomCmdButton.REWIND_10S.commandButton)
            add(NotificationCustomCmdButton.FORWARD_10S.commandButton)
            add(NotificationCustomCmdButton.LOOP.commandButton)
        }.build()
    }

    override fun addNotificationActions(
        mediaSession: MediaSession,
        mediaButtons: ImmutableList<CommandButton>,
        builder: NotificationCompat.Builder,
        actionFactory: MediaNotification.ActionFactory,
    ): IntArray {
        // Simply use our getMediaButtons implementation directly
        val buttons = getMediaButtons(
            mediaSession,
            mediaSession.player.availableCommands,
            mediaButtons,
            !mediaSession.player.isPlaying
        )

        return super.addNotificationActions(
            mediaSession,
            buttons,
            builder,
            actionFactory
        )
    }
}
