package younesbouhouche.musicplayer.dialog.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.ui.theme.AppTheme


class DialogActivity : ComponentActivity() {
    private lateinit var viewModel: DialogVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.action.equals(Intent.ACTION_VIEW) or (intent.data == null)) {
            finish()
            return
        }
        val uri = intent.data!!
        window.setLayout(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        enableEdgeToEdge()
        setContent {
            viewModel = koinViewModel<DialogVM>()
            val card = viewModel.card.collectAsState().value
            val state = viewModel.state.collectAsState().value
            LaunchedEffect(Unit) {
                viewModel.play(uri)
            }
            AppTheme {
                DialogContent(
                    card,
                    state,
                    viewModel::pauseResume,
                    viewModel::seekTo,
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stop()
    }
}