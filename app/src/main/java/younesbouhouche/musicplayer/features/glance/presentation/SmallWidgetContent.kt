package younesbouhouche.musicplayer.features.glance.presentation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
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
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.MainActivity
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.glance.presentation.util.MyImage
import younesbouhouche.musicplayer.features.glance.presentation.util.RowIconButton
import younesbouhouche.musicplayer.features.glance.presentation.util.WidgetText
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEvent
import younesbouhouche.musicplayer.features.player.domain.models.PlayState
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState


@SuppressLint("RestrictedApi")
@Composable
fun SmallWidgetContent(
    card: Song?,
    state: PlayerState,
    onEvent: suspend (PlayerEvent) -> Unit,
    opacity: Float,
    modifier: GlanceModifier = GlanceModifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val background = GlanceTheme.colors.widgetBackground.getColor(context).copy(alpha = opacity)
    Scaffold(
        modifier.clickable(actionStartActivity<MainActivity>()),
        backgroundColor = ColorProvider(background)
    ) {
        Row(
            GlanceModifier.padding(8.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyImage(/*card?.coverUri*/null, opacity = opacity)
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
            RowIconButton(
                icon =
                    if (state.playState == PlayState.PLAYING) R.drawable.pause_icon
                    else R.drawable.play_icon,
                size = 60.dp,
                containerColor = GlanceTheme.colors.primary,
                contentColor = GlanceTheme.colors.onPrimary
            ) {
                scope.launch {
                    onEvent(PlayerEvent.PauseResume)
                }
            }
        }
    }
}