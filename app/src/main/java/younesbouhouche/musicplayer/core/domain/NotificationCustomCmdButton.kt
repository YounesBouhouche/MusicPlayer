package younesbouhouche.musicplayer.core.domain

import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import younesbouhouche.musicplayer.R

const val CUSTOM_COMMAND_REWIND_ACTION_ID = "REWIND_15"
const val CUSTOM_COMMAND_FORWARD_ACTION_ID = "FAST_FWD_15"

enum class NotificationCustomCmdButton(
    val customAction: String,
    val commandButton: CommandButton,
) {
    REWIND(
        customAction = CUSTOM_COMMAND_REWIND_ACTION_ID,
        commandButton = CommandButton.Builder()
            .setDisplayName("Rewind")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_REWIND_ACTION_ID, Bundle()))
            .setIconResId(R.drawable.baseline_replay_10_24)
            .build(),
    ),
    FORWARD(
        customAction = CUSTOM_COMMAND_FORWARD_ACTION_ID,
        commandButton = CommandButton.Builder()
            .setDisplayName("Forward")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_FORWARD_ACTION_ID, Bundle()))
            .setIconResId(R.drawable.baseline_forward_10_24)
            .build(),
    );
}