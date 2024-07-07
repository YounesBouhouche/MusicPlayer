package younesbouhouche.musicplayer.core.domain

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList

@UnstableApi
class CustomMediaNotificationProvider(context: Context) : DefaultMediaNotificationProvider(context) {
    override fun addNotificationActions(
        mediaSession: MediaSession,
        mediaButtons: ImmutableList<CommandButton>,
        builder: NotificationCompat.Builder,
        actionFactory: MediaNotification.ActionFactory
    ): IntArray {
        val defaultPlayPauseCommandButton = mediaButtons.getOrNull(0)
        val notificationMediaButtons = if (defaultPlayPauseCommandButton != null) {
            ImmutableList.builder<CommandButton>().apply {
                add(NotificationCustomCmdButton.REWIND.commandButton)
                add(defaultPlayPauseCommandButton)
                add(NotificationCustomCmdButton.FORWARD.commandButton)
            }.build()
        } else mediaButtons
        return super.addNotificationActions(
            mediaSession,
            notificationMediaButtons,
            builder,
            actionFactory
        )
    }
}