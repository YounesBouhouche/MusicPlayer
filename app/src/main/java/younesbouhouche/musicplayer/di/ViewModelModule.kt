package younesbouhouche.musicplayer.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import younesbouhouche.musicplayer.main.presentation.viewmodel.FavoritesViewModel
import younesbouhouche.musicplayer.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.main.presentation.viewmodel.SearchVM
import younesbouhouche.musicplayer.settings.presentation.routes.language.LanguageViewModel
import younesbouhouche.musicplayer.settings.presentation.routes.playback.PlaybackSettingsViewModel
import younesbouhouche.musicplayer.settings.presentation.routes.player.PlayerSettingsViewModel
import younesbouhouche.musicplayer.settings.presentation.routes.theme.ThemeViewModel

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::ThemeViewModel)
    viewModelOf(::LanguageViewModel)
    viewModelOf(::PlayerSettingsViewModel)
    viewModelOf(::PlaybackSettingsViewModel)
    viewModelOf(::SearchVM)
}