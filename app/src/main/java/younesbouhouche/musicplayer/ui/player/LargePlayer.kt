package younesbouhouche.musicplayer.ui.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import soup.compose.material.motion.MaterialSharedAxisZ
import younesbouhouche.musicplayer.events.PlayerEvent
import younesbouhouche.musicplayer.events.UiEvent
import younesbouhouche.musicplayer.models.MusicCard
import younesbouhouche.musicplayer.removeLeadingTime
import younesbouhouche.musicplayer.states.PlayState
import younesbouhouche.musicplayer.states.PlayerState
import younesbouhouche.musicplayer.states.PlaylistViewState
import younesbouhouche.musicplayer.timeString
import younesbouhouche.musicplayer.toDp
import younesbouhouche.musicplayer.toMs
import younesbouhouche.musicplayer.ui.isCompact
import younesbouhouche.musicplayer.ui.navBarHeight
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LargePlayer(
    queue: List<MusicCard>,
    playerState: PlayerState,
    onPlayerEvent: (PlayerEvent) -> Unit,
    lyrics: Boolean,
    syncing: Boolean,
    onUiEvent: (UiEvent) -> Unit,
    playlistState: AnchoredDraggableState<PlaylistViewState>,
    playlistProgress: Float,
    playlistDragEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val activeItem = queue[playerState.index]
    val pagerState = rememberPagerState(playerState.index) { queue.count() }
    val navBarHeight = navBarHeight
    var height by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val offset = with(density) {
        -((height - 72.dp.roundToPx()) * playlistProgress).roundToInt()
    }
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    var isScrolledByUser by remember { mutableStateOf(false) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }.collect { isScrolling ->
            if (isScrolledByUser && !isScrolling)
                if (pagerState.settledPage != playerState.index)
                    onPlayerEvent(PlayerEvent.Seek(pagerState.currentPage, 0))
            isScrolledByUser = isScrolling && isDragged
        }
    }
    LaunchedEffect(key1 = playerState.index) {
        launch { pagerState.animateScrollToPage(playerState.index) }
    }
    val m = Modifier
        .offset { IntOffset(0, offset) }
        .onGloballyPositioned { height = it.size.height }
        .padding(bottom = 80.dp + navBarHeight)
        .fillMaxSize()
        .background(
            MaterialTheme.colorScheme.background,
            RoundedCornerShape(bottomEnd = 60.dp, bottomStart = 60.dp)
        )
        .clip(RoundedCornerShape(bottomEnd = 60.dp, bottomStart = 60.dp))
        .clipToBounds()
    Box(modifier) {
        if (isCompact)
            Column(m.statusBarsPadding(), verticalArrangement = Arrangement.Center) {
                Pager(
                    lyrics,
                    syncing,
                    pagerState,
                    queue,
                    playerState.index,
                    playerState.time,
                    onPlayerEvent,
                    onUiEvent,
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
                Spacer(Modifier.height(8.dp))
                Controls(
                    activeItem,
                    playerState,
                    onPlayerEvent,
                    Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }
        else
            Row(m.statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                Pager(
                    lyrics,
                    syncing,
                    pagerState,
                    queue,
                    playerState.index,
                    playerState.time,
                    onPlayerEvent,
                    onUiEvent,
                    Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .aspectRatio(1f, true)
                )
                VerticalDivider(Modifier.fillMaxHeight())
                Controls(
                    activeItem,
                    playerState,
                    onPlayerEvent,
                    Modifier.weight(1f)
                )
            }
        val playlistOffset =
            if (playlistState.offset.isNaN()) height.toFloat()
            else playlistState.offset
        Queue(
            queue,
            playerState,
            lyrics,
            playlistState.settledValue == PlaylistViewState.COLLAPSED,
            onPlayerEvent,
            onUiEvent,
            playlistProgress,
            Modifier
                .fillMaxWidth()
                .height(height.toDp() + 72.dp)
                .offset {
                    IntOffset(
                        0,
                        playlistOffset.roundToInt()
                    )
                }
                .anchoredDraggable(playlistState, Orientation.Vertical, playlistDragEnabled)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Pager(
    lyrics: Boolean,
    syncing: Boolean,
    pagerState: PagerState,
    queue: List<MusicCard>,
    index: Int,
    time: Long,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val lyricsLineRegex = Regex("^(\\[((\\d{2}:)?\\d{2}:\\d{2}([.:])\\d{2})])\\s[\\w\\s]*")
    val lyricsRegex = Regex("\\[((\\d{2}:)?\\d{2}:\\d{2}([.:])\\d{2})]")
    var currentLine by remember { mutableIntStateOf(0) }
    val lyricsText = queue[index].lyrics
    val lyricsLines = lyricsText
        .split("\n")
        .filter { it.isNotBlank() }
        .sortedBy {
            lyricsRegex.find(it)?.value?.removeSurrounding("[", "]")?.toMs() ?: 0
        }
    val synced = lyricsLines.any { it.matches(lyricsLineRegex) }
    val lyricsListState = rememberLazyListState()
    val isDragged by lyricsListState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(currentLine, syncing) {
        if (syncing)
            lyricsListState.animateScrollToItem(currentLine)
    }
    LaunchedEffect(key1 = isDragged) {
        if (isDragged) onUiEvent(UiEvent.DisableSyncing)
    }
    val scope = rememberCoroutineScope()
    AnimatedContent(
        targetState = lyrics, label = "",
        modifier = modifier.padding(bottom = 8.dp),
        contentAlignment = Alignment.Center,
        transitionSpec = {
            if (targetState > initialState)
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            else
                slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
        }) { l ->
        if (l) {
            if (lyricsText.isEmpty())
                Text(
                    text = "No lyrics available",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            else if (synced) {
                val segments =
                    lyricsLines.map {
                        lyricsRegex
                            .find(it)
                            ?.value
                            ?.removeSurrounding("[", "]")
                            ?.toMs() ?: 0
                    }
                LaunchedEffect(key1 = time) {
                    if (syncing) currentLine = getIndex(segments, time)
                }
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .weight(1f), state = lyricsListState) {
                        itemsIndexed(lyricsLines) { index, line ->
                            val scale by animateFloatAsState(
                                if ((!syncing) or (index == currentLine)) 1f
                                else 0.5f,
                                label = ""
                            )
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .alpha(scale)
                                    .clickable {
                                        scope.launch {
                                            onPlayerEvent(PlayerEvent.SeekTime(segments[index]))
                                            onUiEvent(UiEvent.EnableSyncing)
                                        }
                                    }
                            ) {
                                Text(
                                    text = line.removeLeadingTime(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    style = MaterialTheme.typography.headlineLarge,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                    AnimatedVisibility(visible = !syncing) {
                        Button(onClick = { onUiEvent(UiEvent.EnableSyncing) }) {
                            Icon(Icons.Default.Timer, null)
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text("Sync to current time")
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lyricsText,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        else
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { queue[it].id }
            ) { page ->
                val pageOffset = (
                        (pagerState.currentPage - page) + pagerState
                            .currentPageOffsetFraction
                        ).absoluteValue
                with(queue[page]) {
                    val fav by favorite.collectAsState()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .scale(
                                lerp(
                                    0.75f,
                                    1f,
                                    1f - pageOffset.coerceIn(0f, 1f)
                                )
                            ),
                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(100))
                                .clipToBounds()
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(100)
                                )
                                .combinedClickable(
                                    onDoubleClick = {
                                        onPlayerEvent(PlayerEvent.SetFavorite(path))
                                    }
                                ) {},
                            contentAlignment = Alignment.Center
                        ) {
                            if (cover != null)
                                Image(
                                    bitmap = cover!!.asImageBitmap(),
                                    null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            else
                                Icon(
                                    Icons.Default.MusicNote,
                                    null,
                                    Modifier.fillMaxSize(.75f),
                                    tint = NavigationBarDefaults.containerColor
                                )
                        }
                        LargeFloatingActionButton(
                            onClick = { onPlayerEvent(PlayerEvent.UpdateFavorite(path, !fav)) },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset(
                                    12.dp,
                                    (-12).dp
                                )
                        ) {
                            AnimatedContent(targetState = fav, label = "") {
                                if (it)
                                    Icon(
                                        Icons.Default.Favorite, null,
                                        Modifier.size(ButtonDefaults.IconSize * 2)
                                    )
                                else
                                    Icon(
                                        Icons.Default.FavoriteBorder, null,
                                        Modifier.size(ButtonDefaults.IconSize * 2)
                                    )
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun Controls(
    activeItem: MusicCard,
    playerState: PlayerState,
    onPlayerEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRemaining by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val nextPrevColors =
        if (isSystemInDarkTheme())
            IconButtonDefaults.filledIconButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiaryContainer,
                containerColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        else
            IconButtonDefaults.filledIconButtonColors(
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
    Column(modifier.padding(horizontal = 30.dp)) {
        Text(
            text = activeItem.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = activeItem.artist,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(Modifier.height(24.dp))
        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            value =
            if ((dragging) or (playerState.loading)) sliderValue
            else (playerState.time.toFloat() / activeItem.duration),
            onValueChange = {
                sliderValue = it
                dragging = true
            },
            onValueChangeFinished = {
                dragging = false
                onPlayerEvent(PlayerEvent.SeekTime((sliderValue * activeItem.duration).roundToLong()))
            }
        )
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { },
                contentPadding = PaddingValues(4.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor =
                    animateColorAsState(
                        targetValue =
                        if (dragging) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(),
                        label = ""
                    ).value
                )
            ) {
                Text(
                    text = (
                            if (dragging) (activeItem.duration * sliderValue).roundToLong()
                            else playerState.time
                            ).timeString,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            MaterialSharedAxisZ(
                targetState = showRemaining,
                forward = true
            ) {
                TextButton(
                    onClick = { showRemaining = !showRemaining },
                    contentPadding = PaddingValues(4.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = (if (it) playerState.time - activeItem.duration else activeItem.duration).timeString,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { onPlayerEvent(PlayerEvent.Previous) },
                modifier = Modifier.size(80.dp),
                colors = nextPrevColors
            ) {
                Icon(
                    Icons.Outlined.SkipPrevious,
                    null,
                    modifier = Modifier.fillMaxSize(.5f)
                )
            }
            FilledIconButton(
                onClick = { onPlayerEvent(PlayerEvent.PauseResume) },
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(100),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primaryContainer,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    if (playerState.playState == PlayState.PLAYING) Icons.Default.Pause
                    else Icons.Default.PlayArrow,
                    null,
                    modifier = Modifier.size(40.dp)
                )
            }
            FilledIconButton(
                onClick = { onPlayerEvent(PlayerEvent.Next) },
                modifier = Modifier.size(80.dp),
                colors = nextPrevColors
            ) {
                Icon(
                    Icons.Outlined.SkipNext,
                    null,
                    modifier = Modifier.fillMaxSize(.5f)
                )
            }
        }
    }
}

fun getIndex(list: List<Long>, time: Long): Int =
    when {
        list.isEmpty() -> -1
        (list.count() == 1) or (list.first() >= time) -> 0
        (list[1] > time) and (list[0] <= time) -> 0
        else -> 1 + getIndex(list - list.first(), time)
    }