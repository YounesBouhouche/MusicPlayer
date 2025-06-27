package younesbouhouche.musicplayer.settings.domain.models

import android.content.Context
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.settings.presentation.util.LocaleUtils

enum class Language(val tag: String, val label: Int) {
    SYSTEM("system", R.string.follow_system),
    ENGLISH("en", R.string.english),
    FRENCH("fr", R.string.french),
    ARABIC("ar", R.string.arabic),
    SPANISH("es", R.string.spanish),
    ITALIAN("it", R.string.italian),
    HINDI("hi", R.string.hindi);

    /**
     * Get the language name in its native form (e.g., "Français" for French)
     *
     * @param context The context to use for resource resolution
     * @return The language name in its native script
     */
    fun getLocalizedName(context: Context): String? {
        return when (this) {
            SYSTEM -> null
            else -> LocaleUtils.getStringInLanguage(context, label, tag)
        }
    }

    companion object {
        fun fromString(value: String): Language =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: SYSTEM
    }
}