package younesbouhouche.musicplayer.features.settings.presentation.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.features.settings.presentation.routes.SettingsRoutes

data class SettingData(
    val headline: Int,
    val supporting: Int? = null,
    val icon: ImageVector? = null,
    val checked: Checked? = null,
    val enabled: Boolean = true,
    val large: Boolean = false,
    val separator: Boolean = false,
    val trailingContent: (@Composable () -> Unit)? = null,
    val bottomContent: (@Composable () -> Unit)? = null,
    val navigateToRoute: SettingsRoutes? = null,
    val onClick: (Context) -> Unit = {},
)

data class Checked(
    val radio: Boolean = false,
    val checked: Boolean,
    val onCheckedChange: (Boolean?) -> Unit
)
