package younesbouhouche.musicplayer.glance.presentation

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent.inject
import younesbouhouche.musicplayer.main.domain.repo.FilesRepo
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState

class MyAppWidget : GlanceAppWidget(), KoinComponent {
    companion object {
        private val SMALL = DpSize(250.dp, 10.dp)
        private val MEDIUM = DpSize(250.dp, 150.dp)
        private val LARGE = DpSize(250.dp, 250.dp)
    }
    override val sizeMode = SizeMode.Responsive(setOf(SMALL, MEDIUM, LARGE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val repo: FilesRepo by inject(FilesRepo::class.java)
                val state = repo.getState().collectAsState(PlayerState()).value
                val currentItem = repo.getCurrentItem().collectAsState(null).value.takeIf {
                    state.playState != PlayState.STOP
                }
                val size = LocalSize.current
                if (size.height > MEDIUM.height)
                    LargeWidgetContent(currentItem, state, repo::onPlayerEvent)
                else if (size.height > SMALL.height)
                    MediumWidgetContent(currentItem, state, repo::onPlayerEvent)
                else
                    SmallWidgetContent(currentItem, state, repo::onPlayerEvent)
            }
        }
    }
}
