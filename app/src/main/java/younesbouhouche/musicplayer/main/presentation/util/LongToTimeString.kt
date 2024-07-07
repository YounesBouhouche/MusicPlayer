package younesbouhouche.musicplayer.main.presentation.util

import kotlin.math.abs

val Long.timeString: String
    get() = String.format(
        "${if (this < 0) "-" else ""}%02d:%02d:%02d",
        (abs(this) / (1000 * 60 * 60)) % 24,
        (abs(this) / (1000 * 60)) % 60,
        (abs(this) / 1000) % 60
    )

val Long.timerString: String
    get() = String.format(
        "${if (this < 0) "-" else ""}%02d:%02d",
        abs(this) / (1000 * 60),
        (abs(this) / 1000) % 60
    )