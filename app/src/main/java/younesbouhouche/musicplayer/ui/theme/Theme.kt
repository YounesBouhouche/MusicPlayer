package younesbouhouche.musicplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import younesbouhouche.musicplayer.settings.data.SettingsDataStore

@Composable
fun AppTheme(
    colors: List<Color>? = null,
    content: @Composable () -> Unit,
) {
    val datastore = SettingsDataStore(LocalContext.current)
    val theme by datastore.theme.collectAsState(initial = "system")
    val isDark =
        when (theme) {
            "light" -> false
            "dark" -> true
            else -> isSystemInDarkTheme()
        }
    if (colors == null) {
        val dynamicColor = datastore.dynamicColors.collectAsState(initial = false).value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        val extraDark by datastore.extraDark.collectAsState(initial = false)
        val colorTheme by datastore.colorTheme.collectAsState(initial = "green")
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
                dynamicColor && isDark -> {
                    if (extraDark) {
                        dynamicDarkColorScheme(LocalContext.current).copy(
                            background = Color.Black,
                            surface = Color.Black,
                        )
                    } else {
                        dynamicDarkColorScheme(LocalContext.current)
                    }
                }
                dynamicColor && !isDark -> {
                    dynamicLightColorScheme(LocalContext.current)
                }
                isDark and extraDark ->
                    darkColors.copy(
                        background = Color.Black,
                        surface = Color.Black,
                    )
                isDark -> darkColors
                else -> lightColors
            }
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    } else {
        val colorScheme =
            if (isDark) {
                darkColorScheme(
                    primary = colors[0],
                    primaryContainer = colors[2],
                    onPrimaryContainer = colors[1],
                    secondary = colors[3],
                    secondaryContainer = colors[5],
                    onSecondaryContainer = colors[4],
                )
            } else {
                lightColorScheme(
                    primary = colors[0],
                    primaryContainer = colors[1],
                    onPrimaryContainer = colors[2],
                    secondary = colors[3],
                    secondaryContainer = colors[4],
                    onSecondaryContainer = colors[5],
                )
            }
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
