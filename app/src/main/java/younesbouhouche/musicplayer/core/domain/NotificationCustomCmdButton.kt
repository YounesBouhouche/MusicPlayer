package younesbouhouche.musicplayer.core.domain

import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.CommandButton.ICON_REPEAT_ONE
import androidx.media3.session.CommandButton.ICON_SKIP_BACK
import androidx.media3.session.CommandButton.ICON_SKIP_BACK_10
import androidx.media3.session.CommandButton.ICON_SKIP_FORWARD
import androidx.media3.session.CommandButton.ICON_SKIP_FORWARD_10
import androidx.media3.session.SessionCommand

// Define custom action IDs for notification buttons
const val CUSTOM_COMMAND_REWIND_10S_ACTION_ID = "REWIND_10S"
const val CUSTOM_COMMAND_FORWARD_10S_ACTION_ID = "FORWARD_10S"
const val CUSTOM_COMMAND_LOOP_ACTION_ID = "TOGGLE_LOOP"
const val CUSTOM_COMMAND_REWIND = "REWIND"
const val CUSTOM_COMMAND_FORWARD = "FORWARD"

/**
 * Custom media notification buttons for player controls
 */
enum class NotificationCustomCmdButton(
    val customAction: String,
    val commandButton: CommandButton,
) {
    REWIND_10S(
        customAction = CUSTOM_COMMAND_REWIND_10S_ACTION_ID,
        commandButton = CommandButton.Builder(ICON_SKIP_BACK_10)
            .setDisplayName("Rewind 10s")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_REWIND_10S_ACTION_ID, Bundle()))
            .build()
    ),
    FORWARD_10S(
        customAction = CUSTOM_COMMAND_FORWARD_10S_ACTION_ID,
        commandButton = CommandButton.Builder(ICON_SKIP_FORWARD_10)
            .setDisplayName("Forward 10s")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_FORWARD_10S_ACTION_ID, Bundle()))
            .build()
    ),
    LOOP(
        customAction = CUSTOM_COMMAND_LOOP_ACTION_ID,
        commandButton = CommandButton.Builder(ICON_REPEAT_ONE)
            .setDisplayName("Toggle Loop")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_LOOP_ACTION_ID, Bundle()))
            .build()
    ),
    REWIND(
        customAction = CUSTOM_COMMAND_REWIND,
        commandButton = CommandButton.Builder(ICON_SKIP_BACK)
            .setDisplayName("Rewind")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_REWIND, Bundle()))
            .build()
    ),
    FORWARD(
        customAction = CUSTOM_COMMAND_FORWARD,
        commandButton = CommandButton.Builder(ICON_SKIP_FORWARD)
            .setDisplayName("Forward")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_FORWARD, Bundle()))
            .build()
    ),
}

