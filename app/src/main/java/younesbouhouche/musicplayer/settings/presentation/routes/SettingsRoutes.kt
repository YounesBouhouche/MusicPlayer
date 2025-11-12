package younesbouhouche.musicplayer.settings.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsRoutes {
    @Serializable
    data object SettingsMain : SettingsRoutes()

    @Serializable
    data object ThemeSettings : SettingsRoutes()

    @Serializable
    data object LanguageSettings : SettingsRoutes()

    @Serializable
    data object PlayerSettings : SettingsRoutes()

    @Serializable
    data object PlaybackSettings : SettingsRoutes()

    @Serializable
    data object AboutSettings : SettingsRoutes()
}