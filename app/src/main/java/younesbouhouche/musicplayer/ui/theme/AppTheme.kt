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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.kmpalette.PaletteState
import com.kmpalette.color
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import younesbouhouche.musicplayer.features.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.features.settings.models.ColorScheme
import younesbouhouche.musicplayer.features.settings.models.Theme

@Composable
internal fun AppTheme(
    primary: Color? = null,
    secondary: Color? = null,
    tertiary: Color? = null,
    content: @Composable () -> Unit,
) {
    val datastore = SettingsDataStore(LocalContext.current)
    val theme by datastore.theme.collectAsState(initial = Theme.SYSTEM)
    val extraDark by datastore.extraDark.collectAsState(initial = false)
    val isDark =
        when (theme) {
            Theme.SYSTEM -> isSystemInDarkTheme()
            Theme.LIGHT -> false
            Theme.DARK -> true
        }
    val colorTheme by datastore.colorTheme.collectAsState(initial = ColorScheme.BLUE)
    val dynamicColor = datastore.dynamicColors.collectAsState(initial = false).value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme =
        when {
            primary != null ->
                rememberDynamicColorScheme(
                    primary = primary,
                    secondary = secondary,
                    tertiary = tertiary,
                    isDark =  isDark,
                    isAmoled = extraDark
                )
            dynamicColor && isDark -> {
                dynamicDarkColorScheme(LocalContext.current)
            }
            dynamicColor && !isDark -> {
                dynamicLightColorScheme(LocalContext.current)
            }
            isDark -> colorTheme.darkScheme
            else -> colorTheme.lightScheme
        }
    DynamicMaterialTheme(
        primary = colorScheme.primary,
        secondary = colorScheme.secondary,
        tertiary = colorScheme.tertiary,
        error = colorScheme.error,
        style = PaletteStyle.Expressive,
        animate = true,
        isDark = isDark,
        isAmoled = extraDark,
        typography = rubikTypography(MaterialTheme.typography),
        content = content
    )
}


@Composable
internal fun AppTheme(
    paletteState: PaletteState<ImageBitmap>,
    content: @Composable () -> Unit,
) {
    val state = paletteState.state
    val palette = paletteState.palette?.takeIf { state != null }
    AppTheme(
        palette?.vibrantSwatch?.color ?: palette?.dominantSwatch?.color,
        palette?.dominantSwatch?.color,
        palette?.mutedSwatch?.color,
        content
    )
}