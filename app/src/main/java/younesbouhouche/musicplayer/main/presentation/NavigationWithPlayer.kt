package younesbouhouche.musicplayer.main.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.main.domain.models.QueueModel
import younesbouhouche.musicplayer.main.domain.models.Routes
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.states.ViewState
import younesbouhouche.musicplayer.main.presentation.util.composables.navBarHeight
import kotlin.math.roundToInt

@Composable
fun BoxScope.NavigationWithPlayer(
    viewHeight: Int,
    queue: QueueModel,
    playerState: PlayerState,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onPlaybackEvent: (PlaybackEvent) -> Unit,
    route: Routes?,
    navigate: (NavRoutes) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val navBarHeight = navBarHeight
    val density = LocalDensity.current
    val state = rememberSaveable(inputs = arrayOf(), saver = AnchoredDraggableState.Saver()) {
        AnchoredDraggableState(
            initialValue = ViewState.HIDDEN,
            anchors =
                DraggableAnchors {
                    ViewState.HIDDEN at viewHeight.toFloat()
                    ViewState.SMALL at with(density) {
                        viewHeight - (202.dp + navBarHeight).toPx()
                    }
                    ViewState.LARGE at 0f
                }
        )
    }
    val progress =
        when (state.settledValue) {
//                        playerState.playState == PlayState.STOP -> 0f
//                        state.settledValue == ViewState.HIDDEN -> 0f
//                        state.offset == 0f -> 1f
            ViewState.SMALL -> state.progress(ViewState.SMALL, ViewState.LARGE)
            ViewState.LARGE -> 1f - state.progress(ViewState.LARGE, ViewState.SMALL)
            else -> 0f
        }
    val offset =
        with(density) {
            ((viewHeight - (202.dp + navBarHeight).roundToPx()) * (1f - progress)).roundToInt()
        }
    val navigationBarOffset = with(density) {
        (progress * (112.dp + navBarHeight).toPx()).roundToInt()
    }
    LaunchedEffect(key1 = viewHeight) {
        state.updateAnchors(
            DraggableAnchors {
                ViewState.HIDDEN at viewHeight.toFloat()
                ViewState.SMALL at with(density) {
                    viewHeight - (202.dp + navBarHeight).toPx()
                }
                ViewState.LARGE at 0f
            },
        )
        state.snapTo(ViewState.SMALL)
    }
    NavBar(
        route,
        playerState.playState != PlayState.STOP,
        Modifier.offset { IntOffset(0, navigationBarOffset) },
        navigate = navigate
    )
    AnimatedVisibility(playerState.playState != PlayState.STOP) {
        PlayerScreen(
            queue,
            playerState,
            offset,
            viewHeight,
            progress,
            playerState,
            state,
            onSetFavorite = { path, favorite ->
                onPlayerEvent(PlayerEvent.UpdateFavorite(path, favorite))
            },
            onPlaybackEvent = onPlaybackEvent
        )
    }
    BackHandler(state.currentValue == ViewState.LARGE) {
        scope.launch {
            state.animateTo(ViewState.SMALL)
        }
    }
}