package younesbouhouche.musicplayer.glance.presentation

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalGlanceId
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.core.domain.player.QueueManager
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.features.main.domain.repo.PlaybackRepository
import younesbouhouche.musicplayer.features.main.presentation.states.PlayState
import younesbouhouche.musicplayer.features.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.features.settings.data.SettingsDataStore

class MyAppWidget : GlanceAppWidget(), KoinComponent {
    companion object {
        private val SMALL = DpSize(250.dp, 10.dp)
        private val MEDIUM = DpSize(250.dp, 150.dp)
        private val LARGE = DpSize(250.dp, 250.dp)
    }
    override val sizeMode = SizeMode.Responsive(setOf(SMALL, MEDIUM, LARGE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val playbackRepository by inject<PlaybackRepository>()
        val mediaRepository by inject<MediaRepository>()
        val stateManager by inject<PlayerStateManager>()
        val queueManager by inject<QueueManager>()
        val initial = queueManager.asyncGetCurrentItem()?.let { id ->
            mediaRepository.suspendGetMediaById(id)
        }
        provideContent {
            GlanceTheme {
                val appWidgetId = LocalGlanceId.current.toString().filter { it.isDigit() }.toInt()
                val dataStore: SettingsDataStore by inject(SettingsDataStore::class.java)
                val opacity by dataStore.getOpacity(appWidgetId).collectAsState(1f)
                val state = stateManager.playerState.collectAsState(PlayerState()).value
                val item by queueManager
                    .getCurrentItem()
                    .map { id ->
                        id?.let {
                            mediaRepository.suspendGetMediaById(it)
                        }
                    }
                    .collectAsState(initial)
                val currentItem = item?.takeIf {
                    state.playState != PlayState.STOP
                }
                val size = LocalSize.current
                if (size.height > MEDIUM.height)
                    LargeWidgetContent(currentItem, state, playbackRepository::onEvent, opacity)
                else if (size.height > SMALL.height)
                    MediumWidgetContent(currentItem, state, playbackRepository::onEvent, opacity)
                else
                    SmallWidgetContent(currentItem, state, playbackRepository::onEvent, opacity)
            }
        }
    }
}

