package younesbouhouche.musicplayer.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import younesbouhouche.musicplayer.features.main.domain.use_cases.*

val useCaseModule = module {
    factoryOf(::GetAlbumsUseCase)
    factoryOf(::GetArtistsUseCase)
    factoryOf(::GetAlbumUseCase)
    factoryOf(::GetArtistUseCase)
    factoryOf(::GetRecentArtistsUseCase)
    factoryOf(::GetHistoryUseCase)
    factoryOf(::GetLastAddedUseCase)
    factoryOf(::GetPlaylistsUseCase)
    factoryOf(::GetPlaylistUseCase)
    factoryOf(::GetQueueUseCase)
    factoryOf(::ScanLibraryUseCase)
    factoryOf(::GetPlayerStateUseCase)
    factoryOf(::GetSongsUseCase)
    factoryOf(::ObserveSongUseCase)
    factoryOf(::GetSongUseCase)
    factoryOf(::ObserveSongUseCase)
    factoryOf(::GetLoadingStateUseCase)
    factoryOf(::SetFavoriteUseCase)
    factoryOf(::HandlePlayerEventUseCase)
    factoryOf(::CreatePlaylistUseCase)
    factoryOf(::AddToPlaylistsUseCase)
    factoryOf(::SetPlaylistSongsUseCase)
}
