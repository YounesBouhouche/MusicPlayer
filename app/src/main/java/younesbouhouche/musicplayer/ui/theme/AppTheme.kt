package younesbouhouche.musicplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import younesbouhouche.musicplayer.settings.data.SettingsDataStore

@Composable
internal fun AppTheme(
    seedColor: Color? = null,
    content: @Composable() () -> Unit,
) {
    val datastore = SettingsDataStore(LocalContext.current)
    val theme by datastore.theme.collectAsState(initial = "system")
    val extraDark by datastore.extraDark.collectAsState(initial = false)
    val isDark =
        when (theme) {
            "light" -> false
            "dark" -> true
            else -> isSystemInDarkTheme()
        }
    val colorTheme by datastore.colorTheme.collectAsState(initial = "green")
    val dynamicColor = datastore.dynamicColors.collectAsState(initial = false).value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val lightColors =
        when (colorTheme) {
            "blue" -> BlueColors.lightScheme
            "green" -> GreenColors.lightScheme
            "red" -> RedColors.lightScheme
            "orange" -> OrangeColors.lightScheme
            "purple" -> PurpleColors.lightScheme
            else -> BlueColors.lightScheme
        }
    val darkColors =
        when (colorTheme) {
            "blue" -> BlueColors.darkScheme
            "green" -> GreenColors.darkScheme
            "red" -> RedColors.darkScheme
            "orange" -> OrangeColors.darkScheme
            "purple" -> PurpleColors.darkScheme
            else -> BlueColors.darkScheme
        }
    val colorScheme =
        when {
            seedColor != null -> rememberDynamicColorScheme(seedColor = seedColor, isDark, extraDark)
            dynamicColor && isDark -> {
                dynamicDarkColorScheme(LocalContext.current)
            }
            dynamicColor && !isDark -> {
                dynamicLightColorScheme(LocalContext.current)
            }
            isDark -> darkColors
            else -> lightColors
        }
    DynamicMaterialTheme(
        primary = colorScheme.primary,
        secondary = colorScheme.secondary,
        tertiary = colorScheme.tertiary,
        animate = true,
        useDarkTheme = isDark,
        withAmoled = extraDark,
        typography = rubikTypography(MaterialTheme.typography),
        content = content
    )
}