package younesbouhouche.musicplayer.core.domain

import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.CommandButton.ICON_REPEAT_ONE
import androidx.media3.session.CommandButton.ICON_SKIP_BACK
import androidx.media3.session.CommandButton.ICON_SKIP_BACK_10
import androidx.media3.session.CommandButton.ICON_SKIP_FORWARD
import androidx.media3.session.CommandButton.ICON_SKIP_FORWARD_10
import androidx.media3.session.SessionCommand


/**
 * Custom media notification buttons for player controls
 */
enum class NotificationCustomCmdButton(
    val customAction: String,
    val commandButton: CommandButton,
) {
    REWIND_10S(
        customAction = CustomCommands.REWIND_10S_ACTION_ID,
        commandButton = CommandButton.Builder(ICON_SKIP_BACK_10)
            .setDisplayName("Rewind 10s")
            .setSessionCommand(SessionCommand(CustomCommands.REWIND_10S_ACTION_ID, Bundle()))
            .build()
    ),
    FORWARD_10S(
        customAction = CustomCommands.FORWARD_10S_ACTION_ID,
        commandButton = CommandButton.Builder(ICON_SKIP_FORWARD_10)
            .setDisplayName("Forward 10s")
            .setSessionCommand(SessionCommand(CustomCommands.FORWARD_10S_ACTION_ID, Bundle()))
            .build()
    ),
    LOOP(
        customAction = CustomCommands.LOOP_ACTION_ID,
        commandButton = CommandButton.Builder(ICON_REPEAT_ONE)
            .setDisplayName("Toggle Loop")
            .setSessionCommand(SessionCommand(CustomCommands.LOOP_ACTION_ID, Bundle()))
            .build()
    ),
    REWIND(
        customAction = CustomCommands.REWIND,
        commandButton = CommandButton.Builder(ICON_SKIP_BACK)
            .setDisplayName("Rewind")
            .setSessionCommand(SessionCommand(CustomCommands.REWIND, Bundle()))
            .build()
    ),
    FORWARD(
        customAction = CustomCommands.FORWARD,
        commandButton = CommandButton.Builder(ICON_SKIP_FORWARD)
            .setDisplayName("Forward")
            .setSessionCommand(SessionCommand(CustomCommands.FORWARD, Bundle()))
            .build()
    ),
}

