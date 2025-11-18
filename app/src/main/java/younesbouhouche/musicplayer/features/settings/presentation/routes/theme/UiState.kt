package younesbouhouche.musicplayer.features.settings.presentation.routes.theme

import younesbouhouche.musicplayer.features.settings.models.ColorScheme
import younesbouhouche.musicplayer.features.settings.models.Theme


data class UiState(
    val themeDialog: Boolean = false,
    val colorDialog: Boolean = false,
    val selectedTheme: Theme = Theme.SYSTEM,
    val selectedColor: ColorScheme = ColorScheme.BLUE,
)
