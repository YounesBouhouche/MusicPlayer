package younesbouhouche.musicplayer.di

import android.content.Context
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import younesbouhouche.musicplayer.main.data.FilesRepoImpl
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.data.PlayerRepoImpl
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.data.db.AppDatabase
import younesbouhouche.musicplayer.main.domain.repo.FilesRepo
import younesbouhouche.musicplayer.main.domain.repo.PlayerRepo
import younesbouhouche.musicplayer.main.presentation.viewmodel.MainVM
import younesbouhouche.musicplayer.settings.data.SettingsDataStore

val appModule = module {
    viewModelOf(::MainVM)
    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "contacts.db").build()
    }

    single<AppDao> {
        get<AppDatabase>().dao
    }

    single { SettingsDataStore(androidContext()) }

    single { PlayerDataStore(androidContext()) }

    single { MediaMetadataRetriever() }

    single { androidContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    single<PlayerRepo> {
        PlayerRepoImpl(
            androidContext(),
            get(),
            get(),
            get()
        )
    }

    single<FilesRepo> {
        FilesRepoImpl(
            androidContext(),
            get(),
            get(),
            get()
        )
    }
}
