package younesbouhouche.musicplayer.glance.presentation

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import androidx.glance.text.Text

class MyAppWidget: GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
//            val viewModel = hiltViewModel<MainVM>()
//            val state by viewModel.playerState.collectAsState()
//            val isPlaying = state.playState != PlayState.STOP
//            Text("Is Playing: $isPlaying")
            Box {
                Text("Hello, World!")
            }
        }
    }
}