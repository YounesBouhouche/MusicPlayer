package younesbouhouche.musicplayer.settings.data

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.settings.domain.models.ColorScheme
import younesbouhouche.musicplayer.settings.domain.models.Language
import younesbouhouche.musicplayer.settings.domain.models.Theme

class SettingsDataStore(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        val THEME_KEY = stringPreferencesKey("app_theme")
        val COLOR_THEME_KEY = stringPreferencesKey("color_theme")
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
        val DYNAMIC_KEY = booleanPreferencesKey("dynamic_theme")
        val EXTRA_DARK_KEY = booleanPreferencesKey("extra_dark")
    }

    val theme = dataFlow(context.dataStore, THEME_KEY, Theme.SYSTEM.name)
        .map { themeName -> Theme.fromString(themeName) }
    val colorTheme = dataFlow(context.dataStore, COLOR_THEME_KEY, ColorScheme.GREEN.name)
        .map { schemeName -> ColorScheme.fromString(schemeName) }
    val dynamicColors = dataFlow(context.dataStore, DYNAMIC_KEY, true)
    val extraDark = dataFlow(context.dataStore, EXTRA_DARK_KEY, false)
    val language = dataFlow(context.dataStore, LANGUAGE_KEY, Language.SYSTEM.name)
        .map { lang -> Language.fromString(lang) }
    fun getOpacity(id: Int) = dataFlow(context.dataStore, floatPreferencesKey("opacity_${id}"), 1f)

    @Composable
    fun isDark(): Flow<Boolean> {
        val isSystemInDarkTheme = isSystemInDarkTheme()
        return theme.map {
            when (it) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme
            }
        }
    }

    suspend fun saveSettings(
        theme: String? = null,
        colorTheme: String? = null,
        dynamic: Boolean? = null,
        extraDark: Boolean? = null,
        language: String? = null,
    ) {
        context.dataStore.edit { preferences ->
            theme?.let { preferences[THEME_KEY] = it }
            colorTheme?.let { preferences[COLOR_THEME_KEY] = it }
            dynamic?.let { preferences[DYNAMIC_KEY] = it }
            extraDark?.let { preferences[EXTRA_DARK_KEY] = it }
            language?.let { preferences[LANGUAGE_KEY] = it }
        }
    }

    suspend fun setOpacity(id: Int, opacity: Float) {
        context.dataStore.edit { preferences ->
            preferences[floatPreferencesKey("opacity_${id}")] = opacity
        }
    }
}
