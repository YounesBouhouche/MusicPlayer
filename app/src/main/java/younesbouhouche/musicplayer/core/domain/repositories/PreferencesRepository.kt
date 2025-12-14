package younesbouhouche.musicplayer.core.domain.repositories

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference

interface PreferencesRepository {
    suspend fun <T, R> set(key: SettingsPreference<T, R>, value: R)
    fun <T, R> get(key: SettingsPreference<T, R>): Flow<R>
}