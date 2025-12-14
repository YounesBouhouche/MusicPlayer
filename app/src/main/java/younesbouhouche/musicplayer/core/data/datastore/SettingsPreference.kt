package younesbouhouche.musicplayer.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.domain.models.preferences.ColorScheme
import younesbouhouche.musicplayer.core.domain.models.preferences.Language
import younesbouhouche.musicplayer.core.domain.models.preferences.Theme

sealed class SettingsPreference<T, R>(
    val key: Preferences.Key<T>,
    val defaultValue: R,
    val mapToResult: (T) -> R = { it as R },
    val mapToStored: (R) -> T = { it as T }
) {
    data object ThemeMode: SettingsPreference<String, Theme>(
        stringPreferencesKey("theme_mode"),
        Theme.SYSTEM,
        {
            Theme.fromString(it)
        },
        {
            it.name
        }
    )
    data object ExtraDarkMode: SettingsPreference<Boolean, Boolean>(
        booleanPreferencesKey("extra_dark_mode"),
        false
    )
    data object ColorSchemeMode: SettingsPreference<String, ColorScheme>(
        stringPreferencesKey("color_scheme"),
        ColorScheme.BLUE,
        {
            ColorScheme.fromString(it)
        }
    )
    data object LanguagePref: SettingsPreference<String, Language>(
        stringPreferencesKey("language"),
        Language.SYSTEM,
        {
            Language.fromString(it)
        },
        {
            it.name
        }
    )
    data object DynamicColor: SettingsPreference<Boolean, Boolean>(
        booleanPreferencesKey("dynamic_color"),
        true,
    )
    data class Opacity(val appWidgetId: Int): SettingsPreference<Float, Float>(
        floatPreferencesKey("widget_opacity_$appWidgetId"),
        1f
    )

    data object RepeatMode:
        SettingsPreference<Int, Int>(intPreferencesKey("repeat_mode"), 0)

    data object SkipSilence:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("skip_silence"), false)

    data object ShuffleMode:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("shuffle_mode"), false)

    data object Speed:
        SettingsPreference<Float, Float>(floatPreferencesKey("playback_speed"), 1f)

    data object Pitch:
        SettingsPreference<Float, Float>(floatPreferencesKey("playback_speed"), 1f)

    data object RememberRepeat:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("remember_repeat"), false)

    data object RememberShuffle:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("remember_shuffle"), false)

    data object RememberSpeed:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("remember_speed"), false)

    data object RememberPitch:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("remember_pitch"), false)

    data object MatchPictureColors:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("MatchPictureColors"), false)
    data object ShowVolumeSlider:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("ShowVolumeSlider"), false)
    data object ShowRepeat:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("ShowRepeat"), false)
    data object ShowShuffle:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("ShowShuffle"), false)
    data object ShowSpeed:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("ShowSpeed"), false)
    data object ShowPitch:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("ShowPitch"), false)
    data object ShowTimer:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("ShowTimer"), false)
    data object ShowLyrics:
        SettingsPreference<Boolean, Boolean>(booleanPreferencesKey("ShowLyrics"), false)

    fun getDataFlow(dataStore: DataStore<Preferences>): Flow<R> {
        return dataStore.data.map { preferences ->
            preferences[key]?.let { mapToResult(it) } ?: defaultValue
        }
    }
    suspend fun setData(dataStore: DataStore<Preferences>, value: R) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[key] = mapToStored(value)
            }
        }
    }
}