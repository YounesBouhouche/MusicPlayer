package younesbouhouche.musicplayer.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import younesbouhouche.musicplayer.core.domain.player.PlayerManager
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.core.domain.player.QueueManager
import younesbouhouche.musicplayer.core.domain.session.MediaSessionManager
import younesbouhouche.musicplayer.main.data.ArtistsRepositoryImpl
import younesbouhouche.musicplayer.main.data.MediaRepositoryImpl
import younesbouhouche.musicplayer.main.data.PlaybackRepositoryImpl
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.data.PlaylistRepositoryImpl
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.domain.repo.ArtistsRepository
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.main.domain.repo.PlaybackRepository
import younesbouhouche.musicplayer.main.domain.repo.PlaylistRepository


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
