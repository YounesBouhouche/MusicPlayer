package younesbouhouche.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import younesbouhouche.musicplayer.core.presentation.theme.AppTheme
import younesbouhouche.musicplayer.features.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.features.player.presentation.service.MediaSessionManager
import younesbouhouche.musicplayer.navigation.MainApp

class MainActivity : ComponentActivity() {
    val sessionManager by inject<MediaSessionManager>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.post {
            lifecycleScope.launch {
                sessionManager.initialize()
            }
        }
        setContent {
            AppTheme {
                SetSystemBarColors()
                MainApp()
            }
        }
    }
}