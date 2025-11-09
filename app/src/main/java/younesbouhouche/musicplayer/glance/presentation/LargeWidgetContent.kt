package younesbouhouche.musicplayer.glance.presentation

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
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.glance.presentation.util.MyImage
import younesbouhouche.musicplayer.glance.presentation.util.RowIconButton
import younesbouhouche.musicplayer.glance.presentation.util.WidgetText
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.presentation.MainActivity
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState


@SuppressLint("RestrictedApi")
@Composable
fun LargeWidgetContent(
    card: MusicCard?,
    state: PlayerState,
    onEvent: suspend (PlaybackEvent) -> Unit,
    opacity: Float,
    modifier: GlanceModifier = GlanceModifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val background = GlanceTheme.colors.background.getColor(context).copy(alpha = opacity)
    Scaffold(
        modifier.clickable(actionStartActivity<MainActivity>()),
        backgroundColor = ColorProvider(background)
    ) {
        Column(
            GlanceModifier.padding(8.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyImage(card?.coverUri, size = 120.dp, opacity = opacity)
            Spacer(GlanceModifier.height(16.dp))
            WidgetText(
                card?.title ?: "No track",
                fontWeight = FontWeight.Bold,
                fontSize = 20f,
                textAlign = TextAlign.Center,
                modifier = GlanceModifier.fillMaxWidth()
            )
            Spacer(GlanceModifier.height(6.dp))
            WidgetText(
                card?.artist ?: "Click to open the app",
                fontWeight = FontWeight.Medium,
                fontSize = 16f,
                textAlign = TextAlign.Center,
                color = GlanceTheme.colors.onSurfaceVariant,
                modifier = GlanceModifier.fillMaxWidth()
            )
            Spacer(GlanceModifier.height(16.dp))
            Row(
                GlanceModifier.padding(8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RowIconButton(
                    icon = R.drawable.baseline_skip_previous_24,
                    size = 60.dp,
                    containerColor = GlanceTheme.colors.tertiary,
                    contentColor = GlanceTheme.colors.onTertiary,
                ) {
                    scope.launch {
                        onEvent(PlaybackEvent.Previous)
                    }
                }
                Spacer(GlanceModifier.width(6.dp))
                RowIconButton(
                    icon =
                        if (state.playState == PlayState.PLAYING) R.drawable.pause_icon
                        else R.drawable.play_icon,
                    size = 60.dp,
                    containerColor = GlanceTheme.colors.primary,
                    contentColor = GlanceTheme.colors.onPrimary
                ) {
                    scope.launch {
                        onEvent(PlaybackEvent.PauseResume)
                    }
                }
                Spacer(GlanceModifier.width(6.dp))
                RowIconButton(
                    icon = R.drawable.baseline_skip_next_24,
                    size = 60.dp,
                    containerColor = GlanceTheme.colors.tertiary,
                    contentColor = GlanceTheme.colors.onTertiary
                ) {
                    scope.launch {
                        onEvent(PlaybackEvent.Next)
                    }
                }
            }
        }
    }
}
