package younesbouhouche.musicplayer.glance.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.MainActivity
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.glance.presentation.util.IconButton
import younesbouhouche.musicplayer.glance.presentation.util.MyImage
import younesbouhouche.musicplayer.glance.presentation.util.WidgetText
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState


@Composable
fun SmallWidgetContent(
    card: MusicCard?,
    state: PlayerState,
    onPlayerEvent: suspend (PlayerEvent) -> Unit,
    modifier: GlanceModifier = GlanceModifier
) {
    val scope = rememberCoroutineScope()
    Scaffold(modifier.clickable(actionStartActivity<MainActivity>())) {
        Row(
            GlanceModifier.padding(8.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyImage(card?.cover)
            Spacer(GlanceModifier.width(16.dp))
            Column(GlanceModifier.fillMaxWidth().defaultWeight()) {
                WidgetText(
                    card?.title ?: "No track",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20f
                )
                Spacer(GlanceModifier.height(6.dp))
                WidgetText(
                    card?.artist ?: "Click to open the app",
                    fontWeight = FontWeight.Medium,
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 16f
                )
            }
            Spacer(GlanceModifier.width(16.dp))
            IconButton(
                icon =
                    if (state.playState == PlayState.PLAYING) R.drawable.pause_icon
                    else R.drawable.play_icon,
                size = 60.dp,
                containerColor = GlanceTheme.colors.primary,
                contentColor = GlanceTheme.colors.onPrimary
            ) {
                scope.launch {
                    onPlayerEvent(PlayerEvent.PauseResume)
                }
            }
        }
    }
}