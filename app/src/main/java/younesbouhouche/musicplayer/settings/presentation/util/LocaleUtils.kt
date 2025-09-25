package younesbouhouche.musicplayer.settings.presentation.util

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import java.util.Locale

/**
 * Utility functions for managing locales and localized strings
 */
object LocaleUtils {
    /**
     * Get a string resource from a specific locale
     *
     * @param context The context to use for resource resolution
     * @param stringId The string resource ID
     * @param locale The desired locale
     * @return The localized string in the specified locale
     */
    fun getStringInLocale(context: Context, @StringRes stringId: Int, locale: Locale): String {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        val localizedContext = context.createConfigurationContext(configuration)
        return localizedContext.getString(stringId)
    }

    /**
     * Get a string resource from a specific language tag
     *
     * @param context The context to use for resource resolution
     * @param stringId The string resource ID
     * @param languageTag The language tag (e.g. "en", "fr", "ar")
     * @return The localized string in the specified locale
     */
    fun getStringInLanguage(context: Context, @StringRes stringId: Int, languageTag: String): String {
        val locale = when (languageTag) {
            "system" -> Locale.getDefault()
            else -> Locale.forLanguageTag(languageTag)
        }
        return getStringInLocale(context, stringId, locale)
    }
}
