package younesbouhouche.musicplayer.core.domain

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import younesbouhouche.musicplayer.R

@UnstableApi
class CustomMediaNotificationProvider(context: Context) : DefaultMediaNotificationProvider(context) {

    override fun getMediaButtons(
        session: MediaSession,
        playerCommands: Player.Commands,
        mediaButtonPreferences: ImmutableList<CommandButton>,
        showPauseButton: Boolean
    ): ImmutableList<CommandButton> {
        // Get the standard play/pause, previous and next buttons
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

        // Create a compact layout with standard buttons in first position
        return ImmutableList.builder<CommandButton>().apply {
            // Standard controls first (most important)
            if (previousButton != null) add(previousButton)
            if (playPauseButton != null) add(playPauseButton)
            if (nextButton != null) add(nextButton)

            // Then our custom controls
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
