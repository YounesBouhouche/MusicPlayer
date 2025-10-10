package younesbouhouche.musicplayer.main.util

fun String.getGroupingKey(): String {
    val trimmed = this.trimStart()
    if (trimmed.isEmpty()) return "?"

    val firstChar = trimmed.first()

    return when {
        // Letters: return uppercase version
        firstChar.isLetter() -> firstChar.uppercaseChar().toString()

        // Digits: group under #
        firstChar.isDigit() -> "#"

        // All other special characters: group under *
        else -> "*"
    }
}