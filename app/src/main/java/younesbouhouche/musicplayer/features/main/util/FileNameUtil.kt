package younesbouhouche.musicplayer.features.main.util

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

fun String.toFileUri(): String {
    return if (this.startsWith("file://")) {
        this
    } else {
        "file://$this"
    }
}
