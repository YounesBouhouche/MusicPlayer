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
//        val startupEvent = intent.getStringExtra("type").toStartupEvent()
        lifecycleScope.launch {
            sessionManager.initialize()
        }
        setContent {
            AppTheme {
                SetSystemBarColors()
                MainApp()
            }
        }
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        if (intent.hasExtra("type")) {
//            when (intent.getStringExtra("type").toStartupEvent()) {
//                StartupEvent.None -> Unit
//                StartupEvent.PlayFavorites -> mainVM.onPlayerEvent(PlayerEvent.PlayFavorites)
//                StartupEvent.PlayMostPlayed -> mainVM.onPlayerEvent(PlayerEvent.PlayMostPlayed)
//                is StartupEvent.PlayPlaylist -> mainVM.onPlayerEvent(
//                    PlayerEvent.PlayPlaylist(intent.getIntExtra("id", -1)),
//                )
//            }
//        }
//    }
}