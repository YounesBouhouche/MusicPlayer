package younesbouhouche.musicplayer.features.settings.presentation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsGraph(val ordinal: Int = 0): NavKey {
    @Serializable
    data object SettingsMain : SettingsGraph(), NavKey

    @Serializable
    data object ThemeSettings : SettingsGraph(1), NavKey

    @Serializable
    data object LanguageSettings : SettingsGraph(1), NavKey

    @Serializable
    data object PlayerSettings : SettingsGraph(1), NavKey

    @Serializable
    data object PlaybackSettings : SettingsGraph(1), NavKey

    @Serializable
    data object AboutSettings : SettingsGraph(1), NavKey

    companion object {
        val allRoutes = listOf(
            SettingsMain,
            ThemeSettings,
            LanguageSettings,
            PlayerSettings,
            PlaybackSettings,
            AboutSettings
        )
    }
}