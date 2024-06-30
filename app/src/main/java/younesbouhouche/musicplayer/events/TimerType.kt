package younesbouhouche.musicplayer.events

sealed interface TimerType {
    data object Disabled: TimerType
    data class Duration(val ms: Long): TimerType
    data class Time(val hour: Int, val min: Int): TimerType
    data class End(val tracks: Int): TimerType
}