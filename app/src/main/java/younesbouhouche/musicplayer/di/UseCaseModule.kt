package younesbouhouche.musicplayer.di

import org.koin.dsl.module
import younesbouhouche.musicplayer.features.main.domain.use_cases.*

val useCaseModule = module {
    factory { GetAlbumsUseCase(get(), get()) }
    factory { GetArtistsUseCase(get(), get()) }
    factory { GetFavoritesUseCase(get()) }
    factory { GetHistoryUseCase(get(), get()) }
    factory { GetLastAddedUseCase(get()) }
    factory { GetMediaByIdUseCase(get()) }
    factory { GetMediaUseCase(get()) }
    factory { GetMostPlayedArtistsUseCase(get(), get()) }
    factory { GetMostPlayedUseCase(get(), get()) }
    factory { GetPlaylistsUseCase(get()) }
    factory { GetPlaylistUseCase(get(), get()) }
    factory { GetQueueUseCase(get(), get()) }
    factory { GetSortedMediaUseCase(get()) }
    factory { GetSortedPlaylistsUseCase(get()) }
    factory { GetSortedPlaylistUseCase(get(), get()) }
    factory { PlaybackControlUseCase(get()) }
    factory { PlaylistControlUseCase(get()) }
    factory { RefreshUseCase(get()) }
    factory { SetFavoriteUseCase(get()) }
    factory { UiControlUseCase(get()) }
}
