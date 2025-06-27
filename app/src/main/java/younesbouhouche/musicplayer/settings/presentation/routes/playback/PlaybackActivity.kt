package younesbouhouche.musicplayer.settings.presentation.routes.playback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import org.koin.android.ext.android.get
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.ui.theme.AppTheme

class PlaybackActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = koinViewModel<PlaybackSettingsViewModel>()
            SetSystemBarColors(get())
            AppTheme {
                PlaybackSettingsScreen(viewModel) {
                    finish()
                }
            }
        }
    }
}