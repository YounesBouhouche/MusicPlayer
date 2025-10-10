package younesbouhouche.musicplayer.main.util

fun Long.toReadableDate(): String {
    val millisecondsInADay = 86_400_000L
    val millisecondsInAYear = 31_536_000_000L
    val currentTime = System.currentTimeMillis()
    val difference = currentTime - this

    return when {
        difference < millisecondsInADay -> "Today"
        difference < 2 * millisecondsInADay -> "Yesterday"
        difference < 7 * millisecondsInADay -> "${difference / millisecondsInADay} days ago"
        difference < millisecondsInAYear -> "${difference / (millisecondsInADay * 7)} weeks ago"
        else -> "${difference / millisecondsInAYear} years ago"
    }
}