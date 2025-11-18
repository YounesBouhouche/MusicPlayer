package younesbouhouche.musicplayer.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import younesbouhouche.musicplayer.core.domain.player.PlayerManager
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.core.domain.player.QueueManager
import younesbouhouche.musicplayer.core.domain.session.MediaSessionManager
import younesbouhouche.musicplayer.features.main.data.ArtistsRepositoryImpl
import younesbouhouche.musicplayer.features.main.data.MediaRepositoryImpl
import younesbouhouche.musicplayer.features.main.data.PlaybackRepositoryImpl
import younesbouhouche.musicplayer.features.main.data.PlayerDataStore
import younesbouhouche.musicplayer.features.main.data.PlaylistRepositoryImpl
import younesbouhouche.musicplayer.features.main.data.dao.AppDao
import younesbouhouche.musicplayer.features.main.domain.repo.ArtistsRepository
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.features.main.domain.repo.PlaybackRepository
import younesbouhouche.musicplayer.features.main.domain.repo.PlaylistRepository


val repoModule = module {
    single<ArtistsRepository> {
        ArtistsRepositoryImpl(get(), get())
    }

    single<MediaRepository> {
        MediaRepositoryImpl(
            androidContext(),
            get<ArtistsRepository>(),
            get<AppDao>()
        )
    }

    single<PlaybackRepository> {
        PlaybackRepositoryImpl(
            get<PlayerManager>(),
            get<PlayerDataStore>(),
            get<MediaSessionManager>(),
            get<PlayerStateManager>(),
            get<QueueManager>()
        )
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(androidContext(), get<AppDao>())
    }
}
