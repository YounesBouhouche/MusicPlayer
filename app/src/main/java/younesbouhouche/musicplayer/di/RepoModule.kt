package younesbouhouche.musicplayer.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import younesbouhouche.musicplayer.core.data.repositories.MusicRepositoryImpl
import younesbouhouche.musicplayer.core.data.repositories.PlaylistRepositoryImpl
import younesbouhouche.musicplayer.core.data.repositories.PreferencesRepositoryImpl
import younesbouhouche.musicplayer.core.data.repositories.QueueRepositoryImpl
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository
import younesbouhouche.musicplayer.core.domain.repositories.PlaylistRepository
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository
import younesbouhouche.musicplayer.core.domain.repositories.QueueRepository
import younesbouhouche.musicplayer.features.player.data.repository.PlayerRepositoryImpl
import younesbouhouche.musicplayer.features.player.domain.repository.PlayerRepository


val repoModule = module {
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
    single<MusicRepository> {
        MusicRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), androidContext()) }
    single<QueueRepository> { QueueRepositoryImpl(get()) }
    single<PlayerRepository> { PlayerRepositoryImpl(get()) }
}
