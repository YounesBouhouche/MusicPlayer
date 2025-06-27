package younesbouhouche.musicplayer.settings.presentation.routes.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.ui.theme.AppTheme

class PlayerActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = koinViewModel<PlayerSettingsViewModel>()
            AppTheme {
                PlayerSettingsScreen(viewModel, onBack = { finish() })
            }
        }
    }
}