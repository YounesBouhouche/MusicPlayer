package younesbouhouche.musicplayer.features.main.domain.events

import java.time.LocalDateTime

sealed interface TimerType {
    data object Disabled : TimerType

    data class Duration(val ms: Long) : TimerType

    data class Time(val hour: Int, val min: Int, val tomorrow: Boolean) : TimerType {
        constructor(hour: Int, min: Int): this(
            hour,
            min,
            LocalDateTime.now().let { now ->
                now.withSecond(0)
                    .withNano(0)
                    .withHour(hour)
                    .withMinute(min)
                    .isBefore(now)
            }
        )
        fun getTargetDate(): LocalDateTime {
            return LocalDateTime.now()
                .withHour(hour)
                .withMinute(min)
                .withSecond(0)
                .withNano(0)
                .plusDays(if (tomorrow) 1 else 0)
        }
        fun getTargetDateMillis(): Long {
            return getTargetDate()
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
    }

    data class End(val tracks: Int) : TimerType
}
