package younesbouhouche.musicplayer.features.player.presentation.service

import androidx.media3.common.Player

class CustomCommandHandler(private val player: Player) {
    fun handleCommand(commandId: String): Boolean {
        when (commandId) {
            CustomCommands.REWIND_10S_ACTION_ID -> {
                val newPosition = (player.currentPosition - 10_000).coerceAtLeast(0)
                player.seekTo(newPosition)
                return true
            }
            CustomCommands.FORWARD_10S_ACTION_ID -> {
                val newPosition = (player.currentPosition + 10_000)
                    .coerceAtMost(if (player.duration > 0) player.duration else player.currentPosition)
                player.seekTo(newPosition)
                return true
            }
            CustomCommands.LOOP_ACTION_ID -> {
                val newRepeatMode = when (player.repeatMode) {
                    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
                    Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
                    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF
                    else -> Player.REPEAT_MODE_OFF
                }
                player.repeatMode = newRepeatMode
                return true
            }
        }
        return false
    }
}