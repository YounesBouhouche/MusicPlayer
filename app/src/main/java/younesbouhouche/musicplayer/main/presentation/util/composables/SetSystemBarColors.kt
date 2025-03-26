package younesbouhouche.musicplayer.main.presentation.util.composables

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import younesbouhouche.musicplayer.settings.data.SettingsDataStore

@Composable
fun ComponentActivity.SetSystemBarColors(dataStore: SettingsDataStore) {
    val isDark by dataStore.isDark().collectAsState(false)
    val scrim = MaterialTheme.colorScheme.scrim
    DisposableEffect(isDark) {
        val statusBarStyle =
            if (isDark) SystemBarStyle.dark(scrim.copy(alpha = .25f).toArgb())
            else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        val navigationBarStyle =
            if (isDark) SystemBarStyle.dark(Color.TRANSPARENT)
            else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = statusBarStyle, navigationBarStyle = navigationBarStyle)
        onDispose { }
    }
}
