package younesbouhouche.musicplayer.glance.presentation

import android.annotation.SuppressLint
import younesbouhouche.musicplayer.R
import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults.defaultTextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent.inject
import younesbouhouche.musicplayer.MainActivity
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
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
                    HorizontalWidgetContent(currentItem, state, repo::onPlayerEvent)
            }
        }
    }
}


@Composable
fun LargeWidgetContent(
    card: MusicCard?,
    state: PlayerState,
    onPlayerEvent: suspend (PlayerEvent) -> Unit,
    modifier: GlanceModifier = GlanceModifier
) {
    val scope = rememberCoroutineScope()
    Scaffold(modifier.clickable(actionStartActivity<MainActivity>())) {
        Column(
            GlanceModifier.padding(8.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyImage(card?.cover, size = 120.dp)
            Spacer(GlanceModifier.height(16.dp))
            MyText(
                card?.title ?: "No track",
                fontWeight = FontWeight.Bold,
                fontSize = 20f,
                textAlign = TextAlign.Center,
                modifier = GlanceModifier.fillMaxWidth()
            )
            Spacer(GlanceModifier.height(6.dp))
            MyText(
                card?.artist ?: "Click to open the app",
                fontWeight = FontWeight.Medium,
                fontSize = 16f,
                textAlign = TextAlign.Center,
                modifier = GlanceModifier.fillMaxWidth()
            )
            Spacer(GlanceModifier.height(16.dp))
            Row(
                GlanceModifier.padding(8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    icon = R.drawable.baseline_skip_previous_24,
                    size = 60.dp,
                    containerColor = GlanceTheme.colors.tertiary,
                    contentColor = GlanceTheme.colors.onTertiary
                ) {
                    scope.launch {
                        onPlayerEvent(PlayerEvent.Previous)
                    }
                }
                Spacer(GlanceModifier.width(12.dp))
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
                Spacer(GlanceModifier.width(12.dp))
                IconButton(
                    icon = R.drawable.baseline_skip_next_24,
                    size = 60.dp,
                    containerColor = GlanceTheme.colors.tertiary,
                    contentColor = GlanceTheme.colors.onTertiary
                ) {
                    scope.launch {
                        onPlayerEvent(PlayerEvent.Next)
                    }
                }
            }
        }
    }
}


@Composable
fun MediumWidgetContent(
    card: MusicCard?,
    state: PlayerState,
    onPlayerEvent: suspend (PlayerEvent) -> Unit,
    modifier: GlanceModifier = GlanceModifier
) {
    val scope = rememberCoroutineScope()
    Scaffold(modifier.clickable(actionStartActivity<MainActivity>())) {
        Column(GlanceModifier.padding(8.dp).fillMaxSize()) {
            Row(
                GlanceModifier.padding(8.dp).defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyImage(card?.cover)
                Spacer(GlanceModifier.width(16.dp))
                Column(GlanceModifier.fillMaxWidth().defaultWeight()) {
                    MyText(
                        card?.title ?: "No track",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20f
                    )
                    Spacer(GlanceModifier.height(6.dp))
                    MyText(
                        card?.artist ?: "Click to open the app",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16f
                    )
                }
                Spacer(GlanceModifier.width(16.dp))
            }
            Row(
                GlanceModifier.padding(8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    icon = R.drawable.baseline_skip_previous_24,
                    size = 60.dp,
                    containerColor = GlanceTheme.colors.tertiary,
                    contentColor = GlanceTheme.colors.onTertiary
                ) {
                    scope.launch {
                        onPlayerEvent(PlayerEvent.Previous)
                    }
                }
                Spacer(GlanceModifier.width(12.dp))
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
                Spacer(GlanceModifier.width(12.dp))
                IconButton(
                    icon = R.drawable.baseline_skip_next_24,
                    size = 60.dp,
                    containerColor = GlanceTheme.colors.tertiary,
                    contentColor = GlanceTheme.colors.onTertiary
                ) {
                    scope.launch {
                        onPlayerEvent(PlayerEvent.Next)
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalWidgetContent(
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
                MyText(
                    card?.title ?: "No track",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20f
                )
                Spacer(GlanceModifier.height(6.dp))
                MyText(
                    card?.artist ?: "Click to open the app",
                    fontWeight = FontWeight.Medium,
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

@Composable
fun MyText(
    text: String,
    modifier: GlanceModifier = GlanceModifier,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: Float? = null,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text,
        modifier,
        style = defaultTextStyle.copy(
            color = GlanceTheme.colors.onBackground,
            fontWeight = fontWeight,
            fontSize = fontSize?.let { TextUnit(fontSize, TextUnitType.Sp) },
            textAlign = textAlign
        ),
        maxLines = 1,
    )
}

@Composable
fun IconButton(
    @DrawableRes
    icon: Int,
    size: Dp,
    modifier: GlanceModifier = GlanceModifier,
    @SuppressLint("RestrictedApi") containerColor: ColorProvider = ColorProvider(Color.Transparent),
    contentColor: ColorProvider = GlanceTheme.colors.onSurface,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier
            .clickable(onClick)
            .cornerRadius(size)
            .background(containerColor)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            ImageProvider(Icon.createWithResource(context, icon)),
            "",
            modifier.size(size / 2),
            colorFilter = ColorFilter.tint(contentColor)
        )
    }
}