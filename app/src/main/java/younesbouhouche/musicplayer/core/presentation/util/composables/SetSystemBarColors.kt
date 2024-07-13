package younesbouhouche.musicplayer.core.presentation.util.composables

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import younesbouhouche.musicplayer.settings.data.SettingsDataStore

@Composable
fun ComponentActivity.SetSystemBarColors(dataStore: SettingsDataStore) {
    val isDark by dataStore.isDark().collectAsState(false)
    DisposableEffect(isDark) {
        enableEdgeToEdge(
            statusBarStyle =
                if (!isDark) {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                } else {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                },
            navigationBarStyle =
                if (!isDark) {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                } else {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                },
        )
        onDispose { }
    }
}
