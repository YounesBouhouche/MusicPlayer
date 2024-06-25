package younesbouhouche.musicplayer.datastores

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> dataFlow(dataStore: DataStore<Preferences>, key: Preferences.Key<T>, default: T): Flow<T> =
    dataStore.data.map {
        it[key] ?: default
    }
