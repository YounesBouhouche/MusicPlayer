package younesbouhouche.musicplayer.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.data.db.AppDatabase
import younesbouhouche.musicplayer.settings.data.SettingsDataStore

val databaseModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(androidContext(), AppDatabase::class.java, "files.db")
            .build()
    }
    single<AppDao> {
        get<AppDatabase>().dao
    }
    single { SettingsDataStore(androidContext()) }
    single { PlayerDataStore(androidContext()) }
}