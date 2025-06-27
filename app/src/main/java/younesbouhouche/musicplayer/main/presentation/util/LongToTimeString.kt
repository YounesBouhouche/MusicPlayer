package younesbouhouche.musicplayer.main.presentation.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

val Long.timeString: String
    get() =
        String.format(
            "${if (this < 0) "-" else ""}%02d:%02d:%02d",
            (abs(this) / (1000 * 60 * 60)) % 24,
            (abs(this) / (1000 * 60)) % 60,
            (abs(this) / 1000) % 60,
        )

val Long.timerString: String
    get() =
        String.format(
            "${if (this < 0) "-" else ""}%02d:%02d",
            abs(this) / (1000 * 60),
            (abs(this) / 1000) % 60,
        )


val Long.timeLabel: String
    get() =
        if (this > 1000 * 60 * 60 * 24) ">1d"
        else if (this < 1000) "<1s"
        else if (this < 60 * 1000) "<1m"
        else StringBuilder().apply {
            ((abs(this@timeLabel) / (1000 * 60 * 60)) % 24).takeIf { it > 0 }?.let { append("${it}h ") }
            ((abs(this@timeLabel) / (1000 * 60)) % 60).takeIf { it > 0 }?.let { append("${it}m ") }
        }.toString()



val Long.sizeLabel: String
    get() = when {
        this < 1024 -> "${this}B"
        this < 1024 * 1024 -> String.format(
            Locale.getDefault(),
            "%dKB",
            (this / 1024.0).roundToInt()
        )
        this < 1024 * 1024 * 1024 -> String.format(
            Locale.getDefault(),
            "%dMB",
            (this / (1024.0 * 1024.0)).roundToInt()
        )
        else -> String.format(
            Locale.getDefault(),
            "%dGB",
            (this / (1024.0 * 1024.0 * 1024.0)).roundToInt()
        )
    }