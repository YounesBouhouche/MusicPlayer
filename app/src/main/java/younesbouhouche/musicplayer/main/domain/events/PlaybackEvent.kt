package younesbouhouche.musicplayer.main.domain.events

import androidx.annotation.FloatRange
import younesbouhouche.musicplayer.core.domain.models.MusicCard

sealed interface PlaybackEvent {
    data object Initialize: PlaybackEvent

    data class Play(val items: List<MusicCard>, val index: Int = 0, val time: Long = 0, val shuffle: Boolean = false) : PlaybackEvent
    data class AddToQueue(val items: List<MusicCard>, val index: Int? = null) : PlaybackEvent

    data class PlayNext(val items: List<MusicCard>) : PlaybackEvent

    data object Resume : PlaybackEvent

    data object Pause : PlaybackEvent

    data object PauseResume : PlaybackEvent

    data object Stop : PlaybackEvent
    data object ClearQueue : PlaybackEvent

    data object Next : PlaybackEvent

    data object Previous : PlaybackEvent

    data class Seek(val index: Int, val time: Long = 0L, val skipIfSameIndex: Boolean = true) :
        PlaybackEvent

    data class SeekTime(val time: Long) : PlaybackEvent

    data class Forward(val ms: Long) : PlaybackEvent

    data class Backward(val ms: Long) : PlaybackEvent

    data class Swap(val from: Int, val to: Int) : PlaybackEvent

    data class Remove(val index: Int) : PlaybackEvent

    data class SetRepeatMode(val repeatMode: Int) : PlaybackEvent

    data class SetSpeed(
        @field:FloatRange(from = 0.0, fromInclusive = false) val speed: Float,
    ) :
        PlaybackEvent

    data class SetPitch(
        @field:FloatRange(from = 0.0, fromInclusive = false) val pitch: Float,
    ) :
        PlaybackEvent

    data object CycleRepeatMode : PlaybackEvent

    data object ToggleShuffle : PlaybackEvent

    data object ResetSpeed : PlaybackEvent

    data class SetTimer(val timer: TimerType) : PlaybackEvent

    data class SetVolume(val volume: Float) : PlaybackEvent

    data object IncreaseVolume : PlaybackEvent

    data object DecreaseVolume : PlaybackEvent

    data class SetPlayerVolume(val volume: Float) : PlaybackEvent
}
