package younesbouhouche.musicplayer.di

import org.koin.dsl.module
import younesbouhouche.musicplayer.features.main.domain.use_cases.*

val useCaseModule = module {
    factory { GetAlbumsUseCase(get()) }
    factory { GetArtistsUseCase(get()) }
    factory { GetAlbumUseCase(get()) }
    factory { GetArtistUseCase(get()) }
    factory { GetHistoryUseCase(get()) }
    factory { GetPlaylistsUseCase(get()) }
    factory { GetPlaylistUseCase(get()) }
    factory { GetQueueUseCase(get()) }
    factory { ScanLibraryUseCase(get()) }
    factory { GetPlayerStateUseCase(get()) }
    factory { GetSongsUseCase(get()) }
    factory { GetSongUseCase(get()) }
    factory { GetSongUseCase(get()) }
    factory { GetLoadingStateUseCase(get()) }
    factory { SetFavoriteUseCase(get()) }
    factory { HandlePlayerEventUseCase(get()) }
}
