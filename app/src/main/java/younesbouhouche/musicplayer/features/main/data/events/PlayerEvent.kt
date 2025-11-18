package younesbouhouche.musicplayer.features.main.data.events

import androidx.annotation.FloatRange
import younesbouhouche.musicplayer.features.main.domain.events.TimerType
import younesbouhouche.musicplayer.core.domain.models.MusicCard

sealed interface PlayerEvent {
    data class AddToQueue(val items: List<MusicCard>) : PlayerEvent

    data class PlayNext(val items: List<MusicCard>) : PlayerEvent

    data class Play(val items: List<MusicCard>, val index: Int = 0, val shuffle: Boolean = false) : PlayerEvent

    data object Resume : PlayerEvent

    data object Pause : PlayerEvent

    data object PauseResume : PlayerEvent

    data object Stop : PlayerEvent

    data object Next : PlayerEvent

    data object Previous : PlayerEvent

    data class Seek(val index: Int, val time: Long = 0L, val skipIfSameIndex: Boolean = true) :
        PlayerEvent

    data class SeekTime(val time: Long) : PlayerEvent

    data class Forward(val ms: Long) : PlayerEvent

    data class Backward(val ms: Long) : PlayerEvent

    data class Swap(val from: Int, val to: Int) : PlayerEvent

    data class Remove(val index: Int) : PlayerEvent

    data class SetRepeatMode(val repeatMode: Int) : PlayerEvent

    data class SetSpeed(
        @field:FloatRange(from = 0.0, fromInclusive = false) val speed: Float,
    ) :
        PlayerEvent

    data class SetPitch(
        @field:FloatRange(from = 0.0, fromInclusive = false) val pitch: Float,
    ) :
        PlayerEvent

    data object CycleRepeatMode : PlayerEvent

    data object ToggleShuffle : PlayerEvent

    data object ResetSpeed : PlayerEvent

    data class SetTimer(val timer: TimerType) : PlayerEvent

    data class UpdateFavorite(val path: String, val favorite: Boolean) : PlayerEvent

    data class SetVolume(val volume: Float) : PlayerEvent

    data object IncreaseVolume : PlayerEvent

    data object DecreaseVolume : PlayerEvent

    data class SetPlayerVolume(val volume: Float) : PlayerEvent
}
