package younesbouhouche.musicplayer.features.main.presentation.player

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import soup.compose.material.motion.animation.materialSharedAxisYIn
import soup.compose.material.motion.animation.materialSharedAxisYOut
import younesbouhouche.musicplayer.core.domain.models.Queue
import younesbouhouche.musicplayer.features.main.presentation.navigation.MainNavRoute
import younesbouhouche.musicplayer.features.main.presentation.navigation.TopLevelRoutes
import younesbouhouche.musicplayer.features.main.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.PlayerViewModel
import younesbouhouche.musicplayer.features.player.domain.models.PlayState
import younesbouhouche.musicplayer.features.player.domain.models.ViewState
import kotlin.math.roundToInt

@Composable
fun BoxScope.NavigationWithPlayer(
    viewHeight: Int,
    route: TopLevelRoutes?,
    navigate: (MainNavRoute) -> Unit,
) {
    val viewModel = koinViewModel<PlayerViewModel>()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val queue by viewModel.queue.collectAsStateWithLifecycle()
    val currentItem by viewModel.currentItem.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val navBarHeight = navBarHeight
    val density = LocalDensity.current
    LaunchedEffect(playerState) {
        println("PlayerState changed: $playerState")
    }
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
            ((viewHeight - (156.dp + navBarHeight).roundToPx()) * (1f - progress)).roundToInt()
        }
    val navigationBarOffset = with(density) {
        (progress * (112.dp + navBarHeight).toPx()).roundToInt()
    }
    LaunchedEffect(key1 = viewHeight) {
        state.updateAnchors(
            DraggableAnchors {
                ViewState.HIDDEN at viewHeight.toFloat()
                ViewState.SMALL at with(density) {
                    viewHeight - (174.dp + navBarHeight).toPx()
                }
                ViewState.LARGE at 0f
            },
        )
        state.snapTo(ViewState.SMALL)
    }
    LaunchedEffect(playerState.playState) {
        if (playerState.playState == PlayState.STOP)
            state.animateTo(ViewState.SMALL)
    }
    AnimatedVisibility(
        playerState.playState != PlayState.STOP,
        enter = materialSharedAxisYIn(true, 100),
        exit = materialSharedAxisYOut(false, 100),
    ) {
        PlayerScreen(
            queue ?: Queue(),
            currentItem,
            playerState,
            offset,
            viewHeight,
            progress,
            state,
            onAction = viewModel::onUiAction,
            onSetFavorite = viewModel::setFavorite,
            onPlayerEvent = viewModel::onPlayerEvent
        )
    }
    NavBar(
        route,
        playerState.playState != PlayState.STOP,
        Modifier.offset { IntOffset(0, navigationBarOffset) },
        navigate = navigate
    )
    BackHandler(state.currentValue == ViewState.LARGE) {
        scope.launch {
            state.animateTo(ViewState.SMALL)
        }
    }
}