package younesbouhouche.musicplayer.features.settings.presentation.routes.theme

import younesbouhouche.musicplayer.core.domain.models.preferences.ColorScheme
import younesbouhouche.musicplayer.core.domain.models.preferences.Theme


data class UiState(
    val themeDialog: Boolean = false,
    val colorDialog: Boolean = false,
    val selectedTheme: Theme = Theme.SYSTEM,
    val selectedColor: ColorScheme = ColorScheme.BLUE,
)
