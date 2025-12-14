package younesbouhouche.musicplayer.core.data.repositories

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.data.datastore.PreferencesDataStore
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository

class PreferencesRepositoryImpl(
    private val dataStore: PreferencesDataStore
): PreferencesRepository {
    override suspend fun <T, R> set(
        key: SettingsPreference<T, R>,
        value: R
    ) {
        dataStore.set(key, value)
    }

    override fun <T, R> get(key: SettingsPreference<T, R>): Flow<R> {
        return dataStore.get(key)
    }
}