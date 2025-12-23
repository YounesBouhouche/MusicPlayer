package younesbouhouche.musicplayer.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import younesbouhouche.musicplayer.features.main.presentation.routes.album.AlbumViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.album.AlbumsViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.artist.ArtistViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.artist.ArtistsViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.home.HomeViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.library.LibraryViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.metadata_editor.MetadataEditorViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.playlist.AddToPlaylistViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.playlist.CreatePlaylistViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.playlist.PlaylistViewModel
import younesbouhouche.musicplayer.features.main.presentation.routes.playlist.PlaylistsViewModel
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.PlayerViewModel
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.SearchVM
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.SongInfoViewModel
import younesbouhouche.musicplayer.features.permissions.presentation.PermissionsViewModel
import younesbouhouche.musicplayer.features.settings.presentation.routes.language.LanguageViewModel
import younesbouhouche.musicplayer.features.settings.presentation.routes.playback.PlaybackSettingsViewModel
import younesbouhouche.musicplayer.features.settings.presentation.routes.player.PlayerSettingsViewModel
import younesbouhouche.musicplayer.features.settings.presentation.routes.theme.ThemeViewModel

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::ThemeViewModel)
    viewModelOf(::LanguageViewModel)
    viewModelOf(::PlayerSettingsViewModel)
    viewModelOf(::PlaybackSettingsViewModel)
    viewModelOf(::SearchVM)
    viewModelOf(::PermissionsViewModel)
    singleOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AlbumsViewModel)
    viewModel { (albumName: String) ->
        AlbumViewModel(get(), get(), albumName)
    }
    viewModelOf(::ArtistsViewModel)
    viewModel { (artistName: String) ->
        ArtistViewModel(get(), get(), artistName)
    }
    viewModelOf(::PlaylistsViewModel)
    viewModel { (playlistId: Long) ->
        PlaylistViewModel(
            get(),
            get(),
            get(),
            playlistId
        )
    }
    viewModelOf(::CreatePlaylistViewModel)
    viewModelOf(::AddToPlaylistViewModel)
    viewModelOf(::LibraryViewModel)
    viewModel { (songId: Long) ->
        SongInfoViewModel(
            get(),
            get(),
            get(),
            songId
        )
    }
    viewModel { (songId: Long) ->
        MetadataEditorViewModel(
            songId,
            get()
        )
    }
    singleOf(::PlayerViewModel)
}