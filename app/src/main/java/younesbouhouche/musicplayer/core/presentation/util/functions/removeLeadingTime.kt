package younesbouhouche.musicplayer.core.presentation.util.functions

fun String.removeLeadingTime(): String
        = when {
    matches(Regex("^(\\[(\\d{2}:\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 12) -> removeRange(0..11)
    matches(Regex("^(\\[(\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 10) -> removeRange(0..9)
    else -> this
}.trimStart()