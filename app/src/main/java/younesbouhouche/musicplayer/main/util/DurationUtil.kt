package younesbouhouche.musicplayer.main.util

import java.util.Locale
import kotlin.math.roundToInt

fun Long.getDurationRange(): Pair<Long, Long> {
    return when (this) {
        in 0L..60_000L -> Pair(0L, 60_000L) // 0s - 1min
        in 60_001L..300_000L -> Pair(60_001L, 300_000L) // 1min - 5min
        in 300_001L..600_000L -> Pair(300_001L, 600_000L) // 5min - 10min
        in 600_001L..1_800_000L -> Pair(600_001L, 1_800_000L) // 10min - 30min
        in 1_800_001L..3_600_000L -> Pair(1_800_001L, 3_600_000L) // 30min - 1h
        else -> Pair(3_600_001L, Long.MAX_VALUE) // > 1h
    }
}

fun Long.toReadableDurationString(): String {
    if (this < 0) return "0s"
    val totalSeconds = (this / 1000.0).roundToInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val parts = mutableListOf<String>()

    if (hours > 0) {
        parts.add(String.format(Locale.getDefault(), "%dh", hours))
    }
    if (minutes > 0) {
        parts.add(String.format(Locale.getDefault(), "%dm", minutes))
    }
    if (seconds > 0 || parts.isEmpty()) {
        parts.add(String.format(Locale.getDefault(), "%ds", seconds))
    }

    return parts.joinToString(" ")
}

fun Pair<Long, Long>.toReadableDurationString(): String {
    val lower = this.first.toReadableDurationString()
    val upper = if (this.second == Long.MAX_VALUE) "∞" else this.second.toReadableDurationString()
    return "$lower - $upper"
}