package younesbouhouche.musicplayer.core.data.datastore

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.domain.models.preferences.Theme

class PreferencesDataStore(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
    }

    @Composable
    fun isDark(): Flow<Boolean> {
        val systemInDarkTheme = isSystemInDarkTheme()
        return get(SettingsPreference.ThemeMode).map { theme ->
            when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> systemInDarkTheme
            }
        }
    }

    suspend fun <T, R> set(
        key: SettingsPreference<T, R>,
        value: R
    ) {
        key.setData(context.dataStore, value)
    }

    fun <T, R> get(key: SettingsPreference<T, R>) = key.getDataFlow(context.dataStore)

}
