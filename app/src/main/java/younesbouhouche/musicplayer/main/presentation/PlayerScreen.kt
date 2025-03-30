package younesbouhouche.musicplayer.main.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import com.kmpalette.color
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.data.util.toBitmap
import younesbouhouche.musicplayer.main.presentation.player.LargePlayer
import younesbouhouche.musicplayer.main.presentation.player.SmallPlayer
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.states.PlaylistViewState
import younesbouhouche.musicplayer.main.presentation.states.UiState
import younesbouhouche.musicplayer.main.presentation.states.ViewState
import younesbouhouche.musicplayer.ui.theme.AppTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    queue: List<MusicCard>,
    index: Int,
    playerState: PlayerState,
    uiState: UiState,
    dragState: AnchoredDraggableState<ViewState>,
    playlistDragState: AnchoredDraggableState<PlaylistViewState>,
    progress: Float,
    playlistProgress: Float,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
) {
    val shape = MaterialTheme.shapes.large.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)
    val offset = if (dragState.offset.isNaN()) 0 else dragState.offset.roundToInt()
    val paletteState = rememberPaletteState()
    val context = LocalContext.current
    val matchPictureColors = PlayerDataStore(context).matchPictureColors.collectAsState(true).value
    val scope = rememberCoroutineScope()
    AppTheme(
        paletteState.palette?.vibrantSwatch?.color ?: paletteState.palette?.dominantSwatch?.color
    ) {
        Box(
            Modifier
                .then(modifier)
                .fillMaxSize()
                .offset {
                    IntOffset(0, offset)
                }
                .anchoredDraggable(dragState, Orientation.Vertical)
                .background(MaterialTheme.colorScheme.surfaceContainer, shape)
                .clip(shape)
                .clipToBounds(),
        ) {
            queue.getOrNull(index)?.run {
                SmallPlayer(
                    queue,
                    index,
                    playerState,
                    onPlayerEvent,
                    Modifier
                        .alpha(1f - progress)
                        .align(Alignment.TopStart)
                        .clickable { onUiEvent(UiEvent.SetViewState(ViewState.LARGE)) },
                ) {
                    if (matchPictureColors)
                        scope.launch {
                            paletteState.generate(it.asImageBitmap())
                        }
                    else
                        paletteState.reset()
                }
                LargePlayer(
                    queue,
                    index,
                    playerState,
                    uiState,
                    onPlayerEvent,
                    uiState.lyricsVisible,
                    uiState.syncing,
                    onUiEvent,
                    playlistDragState,
                    playlistProgress,
                    dragState.settledValue == ViewState.LARGE,
                    Modifier.alpha(progress),
                )
            }
        }
    }
}
