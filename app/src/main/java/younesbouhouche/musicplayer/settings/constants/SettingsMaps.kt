package younesbouhouche.musicplayer.settings.constants

import younesbouhouche.musicplayer.R

object SettingsMaps {
    val languages =
        mapOf(
            "system" to R.string.follow_system,
            "en" to R.string.english,
            "fr" to R.string.french,
            "ar" to R.string.arabic,
            "es" to R.string.spanish,
            "it" to R.string.italian,
            "in" to R.string.hindi,
        )
    val themeOptions =
        listOf(
            "light" to R.string.light,
            "system" to R.string.default_theme,
            "dark" to R.string.dark,
        )
    val colors =
        listOf(
            "blue" to R.string.blue,
            "green" to R.string.green,
            "red" to R.string.red,
            "orange" to R.string.orange,
            "purple" to R.string.purple,
        )
}