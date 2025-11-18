package younesbouhouche.musicplayer.features.settings.models

import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.ui.theme.BlueColors
import younesbouhouche.musicplayer.ui.theme.GreenColors
import younesbouhouche.musicplayer.ui.theme.OrangeColors
import younesbouhouche.musicplayer.ui.theme.PurpleColors
import younesbouhouche.musicplayer.ui.theme.RedColors

enum class ColorScheme(
    val label: Int,
    val lightScheme: androidx.compose.material3.ColorScheme,
    val darkScheme: androidx.compose.material3.ColorScheme
) {
    GREEN(R.string.green, GreenColors.lightScheme, GreenColors.darkScheme),
    BLUE(R.string.blue, BlueColors.lightScheme, BlueColors.darkScheme),
    RED(R.string.red, RedColors.lightScheme, RedColors.darkScheme),
    ORANGE(R.string.orange, OrangeColors.lightScheme, OrangeColors.darkScheme),
    PURPLE(R.string.purple, PurpleColors.lightScheme, PurpleColors.darkScheme);
//    PINK(R.string.pink),
//    YELLOW(R.string.yellow),
//    TEAL(R.string.teal),
//    CYAN(R.string.cyan),
//    BROWN(R.string.brown),
//    GRAY(R.string.gray),
//    BLACK(R.string.black);

    companion object {
        fun fromString(value: String): ColorScheme =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: BLUE
    }
    fun scheme(isDark: Boolean) =
        if (isDark) darkScheme else lightScheme
}