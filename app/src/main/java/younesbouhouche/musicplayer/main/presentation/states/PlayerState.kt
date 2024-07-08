package younesbouhouche.musicplayer.main.presentation.states

import androidx.media3.common.Player
import younesbouhouche.musicplayer.main.domain.events.TimerType

data class PlayerState(
    val index: Int = -1,
    val time: Long = 0,
    val loading: Boolean = false,
    val playState: PlayState = PlayState.STOP,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val shuffle: Boolean = false,
    val speed: Float = 1f,
    var timer: TimerType = TimerType.Disabled,
)

enum class ViewState {
    HIDDEN,
    SMALL,
    LARGE,
}

enum class PlaylistViewState {
    COLLAPSED,
    EXPANDED,
}

enum class PlayState {
    PLAYING,
    PAUSED,
    STOP,
}
