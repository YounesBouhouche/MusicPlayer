package younesbouhouche.musicplayer.features.main.presentation.util.composables

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import org.koin.compose.koinInject
import younesbouhouche.musicplayer.core.data.datastore.PreferencesDataStore
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.domain.models.preferences.Theme
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository

@Composable
fun ComponentActivity.SetSystemBarColors() {
    val repository = koinInject<PreferencesRepository>()
    val theme by repository.get(SettingsPreference.ThemeMode).collectAsState(Theme.SYSTEM)
    val isDark = when(theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM -> isSystemInDarkTheme()
    }
    val scrim = MaterialTheme.colorScheme.scrim
    DisposableEffect(isDark) {
        val statusBarStyle =
            if (isDark) SystemBarStyle.dark(scrim.copy(alpha = 0f).toArgb())
            else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        val navigationBarStyle =
            if (isDark) SystemBarStyle.dark(Color.TRANSPARENT)
            else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = statusBarStyle, navigationBarStyle = navigationBarStyle)
        onDispose { }
    }
}
