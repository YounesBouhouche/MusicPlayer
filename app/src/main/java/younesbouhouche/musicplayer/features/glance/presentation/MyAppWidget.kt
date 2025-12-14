package younesbouhouche.musicplayer.features.glance.presentation

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
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.features.player.domain.models.PlayState
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository
import younesbouhouche.musicplayer.core.domain.repositories.QueueRepository
import younesbouhouche.musicplayer.features.main.domain.use_cases.HandlePlayerEventUseCase

class MyAppWidget : GlanceAppWidget(), KoinComponent {
    companion object {
        private val SMALL = DpSize(250.dp, 10.dp)
        private val MEDIUM = DpSize(250.dp, 150.dp)
        private val LARGE = DpSize(250.dp, 250.dp)
    }
    override val sizeMode = SizeMode.Responsive(setOf(SMALL, MEDIUM, LARGE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stateManager by inject<PlayerStateManager>()
        val handlePlayerEventUseCase by inject<HandlePlayerEventUseCase>()
        val queueRepository by inject<QueueRepository>()
        val settingsRepository by inject<PreferencesRepository>()
        val initial = queueRepository.getQueue()?.getCurrentItem()
        provideContent {
            GlanceTheme {
                val appWidgetId = LocalGlanceId.current.toString().filter { it.isDigit() }.toInt()
                val opacity by settingsRepository.get(SettingsPreference.Opacity(appWidgetId))
                    .collectAsState(1f)
                val state = stateManager.playerState.collectAsState(PlayerState()).value
                val item by queueRepository.observeQueue().map { it?.getCurrentItem() }
                    .collectAsState(initial)
                val currentItem = item?.takeIf {
                    state.playState != PlayState.STOP
                }
                val size = LocalSize.current
                if (size.height > MEDIUM.height)
                    LargeWidgetContent(
                        currentItem,
                        state,
                        { handlePlayerEventUseCase(it) },
                        opacity
                    )
                else if (size.height > SMALL.height)
                    MediumWidgetContent(
                        currentItem,
                        state,
                        { handlePlayerEventUseCase(it) },
                        opacity
                    )
                else
                    SmallWidgetContent(
                        currentItem,
                        state,
                        { handlePlayerEventUseCase(it) },
                        opacity
                    )
            }
        }
    }
}

