package younesbouhouche.musicplayer.settings.presentation.routes.theme

import younesbouhouche.musicplayer.settings.domain.models.ColorScheme
import younesbouhouche.musicplayer.settings.domain.models.Theme

data class UiState(
    val themeDialog: Boolean = false,
    val colorDialog: Boolean = false,
    val selectedTheme: Theme = Theme.SYSTEM,
    val selectedColor: ColorScheme = ColorScheme.BLUE,
)
