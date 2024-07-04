package younesbouhouche.musicplayer.ui.screens

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.MainActivity
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.events.FilesEvent
import younesbouhouche.musicplayer.events.PlayerEvent
import younesbouhouche.musicplayer.events.PlaylistEvent
import younesbouhouche.musicplayer.events.UiEvent
import younesbouhouche.musicplayer.models.NavRoutes
import younesbouhouche.musicplayer.states.PlayState
import younesbouhouche.musicplayer.states.PlaylistViewState
import younesbouhouche.musicplayer.states.ViewState
import younesbouhouche.musicplayer.ui.components.ItemBottomSheet
import younesbouhouche.musicplayer.ui.components.ListBottomSheet
import younesbouhouche.musicplayer.ui.components.PlaylistBottomSheet
import younesbouhouche.musicplayer.ui.dialogs.AddToPlaylistDialog
import younesbouhouche.musicplayer.ui.dialogs.MetadataDialog
import younesbouhouche.musicplayer.ui.dialogs.NewPlaylistDialog
import younesbouhouche.musicplayer.ui.dialogs.SpeedDialog
import younesbouhouche.musicplayer.ui.dialogs.TimerDialog
import younesbouhouche.musicplayer.ui.leftEdgeWidth
import younesbouhouche.musicplayer.ui.navBarHeight
import younesbouhouche.musicplayer.viewmodel.MainVM

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    granted: Boolean,
    onPermissionRequest: () -> Unit,
    mainVM: MainVM,
    navController: NavHostController,
    isParent: Boolean,
    navigationState: Int,
) {
    val context = LocalContext.current
    val loading by mainVM.loading.collectAsState()
    val queue by mainVM.queue.collectAsState()
    val searchState by mainVM.searchState.collectAsState()
    val playerState by mainVM.playerState.collectAsState()
    val uiState by mainVM.uiState.collectAsState()
    val playlists by mainVM.playlists.collectAsState()
    val isPlaying = playerState.playState != PlayState.STOP
    var height by remember { mutableIntStateOf(0) }
    val navBarHeight = navBarHeight
    val density = LocalDensity.current
    val playlist by mainVM.bottomSheetPlaylist.collectAsState()
    val playlistFiles by mainVM.bottomSheetPlaylistFiles.collectAsState()
    val isCompact =
        currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val offset = with (density) {
        height - ((if (isParent and isCompact and !searchState.expanded) 160.dp else 80.dp) + navBarHeight).roundToPx()
    }
    val playlistOffset = with (density) {
        height - (72.dp + navBarHeight).roundToPx()
    }
    val state = rememberSaveable(
        saver = AnchoredDraggableState.Saver(
            tween(),
            splineBasedDecay(density),
            { it * .5f },
            { with (density) { 100.dp.toPx() } }
        )
    ) {
        AnchoredDraggableState(
            initialValue = ViewState.HIDDEN,
            anchors = DraggableAnchors {},
            snapAnimationSpec = tween(),
            decayAnimationSpec = splineBasedDecay(density),
            positionalThreshold = { it * .5f },
            velocityThreshold = { with (density) { 100.dp.toPx() } }
        )
    }
    val playlistState = rememberSaveable(
        saver = AnchoredDraggableState.Saver(
            tween(),
            splineBasedDecay(density),
            { it * .5f },
            { with (density) { 100.dp.toPx() } }
        )
    ) {
        AnchoredDraggableState(
            initialValue = PlaylistViewState.COLLAPSED,
            anchors = DraggableAnchors {
                PlaylistViewState.COLLAPSED at playlistOffset.toFloat()
                PlaylistViewState.EXPANDED at 0f
                                       },
            snapAnimationSpec = tween(),
            decayAnimationSpec = splineBasedDecay(density),
            positionalThreshold = { it * .5f },
            velocityThreshold = { with (density) { 100.dp.toPx() } }
        )
    }
    LaunchedEffect(key1 = offset) {
        val snap = state.settledValue == ViewState.HIDDEN
        state.updateAnchors(DraggableAnchors {
            ViewState.HIDDEN at height.toFloat()
            ViewState.SMALL at offset.toFloat()
            ViewState.LARGE at 0f
        })
        if (snap) launch { state.snapTo(ViewState.HIDDEN) }
        playlistState.updateAnchors(DraggableAnchors {
            PlaylistViewState.COLLAPSED at playlistOffset.toFloat()
            PlaylistViewState.EXPANDED at 0f
        })
    }
    LaunchedEffect(key1 = state.settledValue) {
        mainVM.onUiEvent(UiEvent.SetViewState(state.settledValue))
    }
    LaunchedEffect(key1 = playlistState.settledValue) {
        mainVM.onUiEvent(UiEvent.SetPlaylistViewState(playlistState.settledValue))
    }
    LaunchedEffect(key1 = uiState.viewState) {
        if ((uiState.viewState == ViewState.HIDDEN) and (playerState.playState != PlayState.STOP))
            mainVM.onPlayerEvent(PlayerEvent.Stop)
        launch {
            state.animateTo(uiState.viewState)
            playlistState.snapTo(PlaylistViewState.COLLAPSED)
        }
    }
    LaunchedEffect(key1 = uiState.playlistViewState) {
        launch { playlistState.animateTo(uiState.playlistViewState) }
    }
    val progress =
        if (playerState.playState == PlayState.STOP) 0f
        else if (state.offset == 0f) 1f
        else if (state.settledValue == ViewState.SMALL)
            state.progress(ViewState.SMALL, ViewState.LARGE)
        else if (state.settledValue == ViewState.LARGE)
            1f - state.progress(ViewState.LARGE, ViewState.SMALL)
        else 0f
    val playlistProgress =
        when (playlistState.settledValue) {
            PlaylistViewState.COLLAPSED -> playlistState.progress(PlaylistViewState.COLLAPSED, PlaylistViewState.EXPANDED)
            PlaylistViewState.EXPANDED -> 1f - playlistState.progress(PlaylistViewState.EXPANDED, PlaylistViewState.COLLAPSED)
        }
    val startPadding = if (isCompact) 0.dp else 80.dp + leftEdgeWidth
    val bottomPadding = if (isCompact and isParent) (80.dp + navBarHeight) else if (!isCompact) navBarHeight else 0.dp
    val playerPadding = (if (isPlaying) 74.dp else 0.dp)
    val pullToRefreshState = rememberPullToRefreshState()
    val cutout = if (younesbouhouche.musicplayer.ui.isCompact) Modifier else Modifier.displayCutoutPadding()
    AnimatedContent(targetState = granted, label = "") { isGranted ->
        if (isGranted)
            Surface(modifier = Modifier
                .onGloballyPositioned { height = it.size.height }
                .onSizeChanged { height = it.height }
                .fillMaxSize()) {
                PullToRefreshBox(
                    state = pullToRefreshState,
                    isRefreshing = loading,
                    onRefresh = {
                        mainVM.onFilesEvent(FilesEvent.LoadFiles)
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavigationScreen(
                        navController,
                        mainVM,
                        Modifier.padding(
                            bottom = bottomPadding + playerPadding,
                            start = startPadding
                        ).then(cutout)
                    )
                    AnimatedVisibility(
                        visible = isParent,
                        enter = slideInVertically { -it },
                        exit = slideOutVertically { -it },
                        modifier = Modifier.padding(start = startPadding).then(cutout)
                    ) {
                        SearchScreen(searchState, loading, mainVM::onSearchEvent)
                    }
                    PlayerScreen(
                        Modifier.padding(start = startPadding),
                        queue,
                        playerState,
                        uiState,
                        state,
                        playlistState,
                        progress,
                        playlistProgress,
                        mainVM::onPlayerEvent,
                        mainVM::onUiEvent
                    )
                    NavBar(
                        visible = isParent and (!searchState.expanded),
                        progress = progress,
                        state = navigationState
                    ) {
                        navController.navigate(it) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        else
            Scaffold {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(onClick = onPermissionRequest) {
                        Text("Request permission")
                    }
                }
            }
    }
    uiState.bottomSheetItem?.run {
        ItemBottomSheet(
            open = uiState.bottomSheetVisible,
            state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = { mainVM.onUiEvent(UiEvent.HideBottomSheet) },
            file = this,
            onPlayerEvent = mainVM::onPlayerEvent,
            onUiEvent = mainVM::onUiEvent,
            navigateToAlbum = {
                mainVM.getAlbum(this)?.let {
                    mainVM.setListFiles(it.items)
                    navController.navigate(NavRoutes.ListScreen(it.title))
                }
            },
            navigateToArtist = {
                mainVM.getArtist(this)?.let {
                    mainVM.setListFiles(it.items)
                    navController.navigate(NavRoutes.ListScreen(it.name))
                }
            },
            shareFile = {
                context.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_STREAM, contentUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            type = "audio/*"
                        }, "Share"
                    )
                )
            }
        )
    }
    uiState.listBottomSheetList?.run {
        ListBottomSheet(
            open = uiState.listBottomSheetVisible,
            list = this,
            onPlayerEvent = mainVM::onPlayerEvent,
            onUiEvent = mainVM::onUiEvent,
            title = uiState.listBottomSheetTitle,
            text = uiState.listBottomSheetText,
            cover = uiState.listBottomSheetImage,
            alternative = uiState.listBottomSheetIcon,
            state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shareFiles = {
                context.startActivity(
                    Intent.createChooser(
                        Intent().apply {
                            action = Intent.ACTION_SEND_MULTIPLE
                            putParcelableArrayListExtra(
                                Intent.EXTRA_STREAM,
                                ArrayList(map { it.contentUri })
                            )
                            type = "audio/*"
                        },
                        null
                    )
                )
            }
        )
    }
    PlaylistBottomSheet(
        open = uiState.playlistBottomSheetVisible,
        state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = { mainVM.onUiEvent(UiEvent.HidePlaylistBottomSheet) },
        title = playlist.name,
        files = playlistFiles,
        onPlayerEvent = mainVM::onPlayerEvent,
        onUiEvent = mainVM::onUiEvent,
        delete = { mainVM.onPlaylistEvent(PlaylistEvent.DeletePlaylist(playlist)) },
        addToHomeScreen = {
            val manager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
            manager.requestPinShortcut(
                ShortcutInfo
                    .Builder(context, "playlist-${playlist.id}")
                    .setIcon(Icon.createWithResource(context, R.drawable.baseline_playlist_play_24))
                    .setShortLabel(playlist.name)
                    .setLongLabel(playlist.name)
                    .setIntent(Intent(context, MainActivity::class.java).apply {
                        action = ACTION_VIEW
                        putExtra("type", "playlist")
                        putExtra("id", playlist.id)
                    })
                    .build(),
                null
            )
        }
    ) {
        context.startActivity(
            Intent.createChooser(
                Intent().apply {
                    action = Intent.ACTION_SEND_MULTIPLE
                    putParcelableArrayListExtra(
                        Intent.EXTRA_STREAM,
                        ArrayList(playlistFiles.map { it.contentUri })
                    )
                    type = "audio/*"
                },
                null
            )
        )
    }
    SpeedDialog(
        uiState.speedDialog,
        { mainVM.onUiEvent(UiEvent.HideSpeedDialog) },
        playerState.speed,
        { mainVM.onPlayerEvent(PlayerEvent.SetSpeed(it)) }
    )
    TimerDialog(
        uiState.timerDialog,
        playerState.timer,
        { mainVM.onUiEvent(UiEvent.HideTimerDialog) },
        { mainVM.onPlayerEvent(PlayerEvent.SetTimer(it)) }
    )
    NewPlaylistDialog(
        uiState.newPlaylistDialog,
        uiState.newPlaylistName,
        { mainVM.onUiEvent(UiEvent.UpdateNewPlaylistName(it)) },
        { mainVM.onUiEvent(UiEvent.HideNewPlaylistDialog) },
        { mainVM.onPlaylistEvent(PlaylistEvent.CreateNew) }
    )
    AddToPlaylistDialog(
        uiState.addToPlaylistDialog,
        playlists,
        uiState.addToPlaylistIndex,
        { mainVM.onUiEvent(UiEvent.UpdateSelectedPlaylist(it)) },
        { mainVM.onUiEvent(UiEvent.HideAddToPlaylistDialog) },
        { mainVM.onPlaylistEvent(PlaylistEvent.AddToPlaylist) }
    )
    MetadataDialog(
        uiState.metadataDialog,
        { mainVM.onUiEvent(UiEvent.HideMetadataDialog) },
        { mainVM.onFilesEvent(FilesEvent.UpdateMetadata(uiState.metadata)) },
        uiState.metadata,
        mainVM::onMetadataEvent
    )
}