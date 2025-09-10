package younesbouhouche.musicplayer.main.presentation

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.MainActivity
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistsUiEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.main.domain.models.Routes
import younesbouhouche.musicplayer.main.presentation.components.ItemBottomSheet
import younesbouhouche.musicplayer.main.presentation.components.ListBottomSheet
import younesbouhouche.musicplayer.main.presentation.components.PlaylistBottomSheet
import younesbouhouche.musicplayer.main.presentation.components.QueueBottomSheet
import younesbouhouche.musicplayer.main.presentation.dialogs.AddToPlaylistDialog
import younesbouhouche.musicplayer.main.presentation.dialogs.CreatePlaylistDialog
import younesbouhouche.musicplayer.main.presentation.dialogs.DetailsDialog
import younesbouhouche.musicplayer.main.presentation.dialogs.MetadataDialog
import younesbouhouche.musicplayer.main.presentation.dialogs.PitchDialog
import younesbouhouche.musicplayer.main.presentation.dialogs.RenamePlaylistDialog
import younesbouhouche.musicplayer.main.presentation.dialogs.SpeedDialog
import younesbouhouche.musicplayer.main.presentation.dialogs.TimerDialog
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlaylistViewState
import younesbouhouche.musicplayer.main.presentation.states.ViewState
import younesbouhouche.musicplayer.main.presentation.util.Event
import younesbouhouche.musicplayer.main.presentation.util.composables.CollectEvents
import younesbouhouche.musicplayer.main.presentation.util.composables.isCompact
import younesbouhouche.musicplayer.main.presentation.util.composables.leftEdgeWidth
import younesbouhouche.musicplayer.main.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.main.presentation.util.composables.rightEdgeWidth
import younesbouhouche.musicplayer.main.presentation.util.shareFiles
import younesbouhouche.musicplayer.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.main.presentation.viewmodel.SearchVM
import younesbouhouche.musicplayer.welcome.presentation.WelcomeScreen

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun AppScreen(
    granted: Boolean,
    onPermissionRequest: () -> Unit,
    mainVM: MainViewModel,
    searchVM: SearchVM,
    navController: NavHostController,
    isParent: Boolean,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentNavRoute =
        currentRoute?.let { route ->
            Routes
                .entries
                .firstOrNull {
                    it.destination.javaClass.kotlin.qualifiedName?.contains(route) == true
                }
        } ?: Routes.Home
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val queue by mainVM.queue.collectAsState()
    val searchState by searchVM.searchState.collectAsState()
    val playerState by mainVM.playerState.collectAsState()
    val uiState by mainVM.uiState.collectAsState()
    val bottomSheetItem by mainVM.bottomSheetItem.collectAsState()
    val playlists by mainVM.playlists.collectAsState()
    val isPlaying = playerState.playState != PlayState.STOP
    var height by remember { mutableIntStateOf(0) }
    val playlist by mainVM.sheetPlaylist.collectAsState()
    val navBarHeight = navBarHeight
    val density = LocalDensity.current
    val offset =
        with(density) {
            height - ((if (isParent and isCompact and !searchState.expanded) 160.dp else 80.dp) +
                    navBarHeight).roundToPx()
        }
    val playlistOffset =
        with(density) {
            height - (72.dp + navBarHeight).roundToPx()
        }
    val state = rememberSaveable(inputs = arrayOf(), saver = AnchoredDraggableState.Saver()) {
        AnchoredDraggableState(initialValue = ViewState.HIDDEN, anchors = DraggableAnchors {})
    }
    val playlistState =
        rememberSaveable(inputs = arrayOf(), saver = AnchoredDraggableState.Saver()) {
            AnchoredDraggableState(
                initialValue = PlaylistViewState.COLLAPSED,
                anchors =
                    DraggableAnchors {
                        PlaylistViewState.COLLAPSED at playlistOffset.toFloat()
                        PlaylistViewState.EXPANDED at 0f
                    }
            )
        }
    LaunchedEffect(key1 = offset) {
        val snap = state.settledValue == ViewState.HIDDEN
        state.updateAnchors(
            DraggableAnchors {
                ViewState.HIDDEN at height.toFloat()
                ViewState.SMALL at offset.toFloat()
                ViewState.LARGE at 0f
            },
        )
        if (snap) launch { state.snapTo(ViewState.HIDDEN) }
        playlistState.updateAnchors(
            DraggableAnchors {
                PlaylistViewState.COLLAPSED at playlistOffset.toFloat()
                PlaylistViewState.EXPANDED at 0f
            },
        )
    }
    LaunchedEffect(key1 = state.targetValue) {
        if (
            (state.targetValue == ViewState.HIDDEN)
                and (state.settledValue != ViewState.HIDDEN)
                and (playerState.playState != PlayState.STOP)
            ) {
            mainVM.onPlaybackEvent(PlaybackEvent.Stop)
        }
        launch {
            playlistState.snapTo(PlaylistViewState.COLLAPSED)
        }
    }
    LaunchedEffect(key1 = uiState.playlistViewState) {
        launch { playlistState.animateTo(uiState.playlistViewState) }
    }
    LaunchedEffect(key1 = playerState.playState) {
        if ((playerState.playState != PlayState.STOP) and (state.settledValue == ViewState.HIDDEN)) {
            launch {
                playlistState.snapTo(PlaylistViewState.COLLAPSED)
                state.animateTo(ViewState.SMALL)
            }
        }
    }
    LaunchedEffect(key1 = playerState.playState) {
        if (playerState.playState == PlayState.STOP) {
            launch { state.animateTo(ViewState.HIDDEN) }
        }
    }
    val progress =
        when {
            playerState.playState == PlayState.STOP -> 0f
            state.offset == 0f -> 1f
            state.settledValue == ViewState.SMALL -> state.progress(ViewState.SMALL, ViewState.LARGE)
            state.settledValue == ViewState.LARGE -> 1f - state.progress(ViewState.LARGE, ViewState.SMALL)
            else -> 0f
        }
    val smallPlayerProgress =
        when {
            playerState.playState == PlayState.STOP -> 0f
            state.offset == 0f -> 1f
            state.settledValue == ViewState.HIDDEN -> state.progress(ViewState.HIDDEN, ViewState.SMALL)
            state.settledValue == ViewState.SMALL -> 1f - state.progress(ViewState.SMALL, ViewState.HIDDEN)
            else -> 1f
        }
    LaunchedEffect(smallPlayerProgress) {
//        if (state.settledValue != ViewState.HIDDEN)
//            mainVM.onPlaybackEvent(PlaybackEvent.SetPlayerVolume(smallPlayerProgress))
    }
    val playlistProgress =
        when (playlistState.settledValue) {
            PlaylistViewState.COLLAPSED ->
                playlistState.progress(PlaylistViewState.COLLAPSED, PlaylistViewState.EXPANDED)
            PlaylistViewState.EXPANDED ->
                1f - playlistState.progress(PlaylistViewState.EXPANDED, PlaylistViewState.COLLAPSED)
        }
    val startPadding = if (isCompact) 0.dp else (80.dp + leftEdgeWidth) * (1f - progress)
    val endPadding = if (isCompact) 0.dp else (rightEdgeWidth * (1f - progress))
    val bottomPadding = if (isCompact and isParent) (80.dp + navBarHeight) else 0.dp
    val playerPadding = (if (isPlaying) 74.dp else 0.dp)
    val pullToRefreshState = rememberPullToRefreshState()
    val cutout = if (isCompact) Modifier else Modifier.displayCutoutPadding()
    AnimatedContent(targetState = granted, label = "") { isGranted ->
        if (isGranted) {
            Surface(
                modifier =
                Modifier
                    .onGloballyPositioned { height = it.size.height }
                    .onSizeChanged { height = it.height }
                    .fillMaxSize()
                    .semantics {
                        testTagsAsResourceId = true
                    }
                    .pullToRefresh(
                        state = pullToRefreshState,
                        enabled = isParent and (state.settledValue != ViewState.LARGE),
                        isRefreshing = false,
                        onRefresh = {
                            mainVM.onReload()
                        },
                    ),
            ) {
                Box(Modifier.fillMaxSize()) {
                    NavigationScreen(
                        navController,
                        mainVM,
                        Modifier
                            .padding(
                                bottom = bottomPadding + playerPadding,
                                start = startPadding,
                            )
                            .then(cutout),
                    )
                    AnimatedVisibility(
                        visible = isParent,
                        enter = slideInVertically { -it },
                        exit = slideOutVertically { -it },
                        modifier =
                            Modifier
                                .padding(start = startPadding)
                                .then(cutout),
                    ) {
                        SearchScreen(
                            searchState,
                            uiState.showAppName,
                            uiState.loading,
                            searchVM::onSearchEvent,
                            {
                                mainVM.onPlaybackEvent(
                                    PlaybackEvent.Play(
                                        searchState.result.files,
                                        it
                                    )
                                )
                            },
                            {
                                navController.navigate(NavRoutes.Album(it.name))
                            },
                            {
                                navController.navigate(NavRoutes.Artist(it.name))
                            },
                            {
                                navController.navigate(NavRoutes.Playlist(it.id))
                            },
                            Modifier,
                            {
                                AnimatedVisibility(currentNavRoute != Routes.Home) {
                                    IconButton(onClick = {
                                        mainVM.onUiEvent(
                                            UiEvent.ShowSortSheet(currentNavRoute)
                                        )
                                    }) {
                                        Icon(
                                            Icons.AutoMirrored.Default.Sort,
                                            null
                                        )
                                    }
                                }
                            }
                        ) {
                            mainVM.onUiEvent(UiEvent.ShowBottomSheet(it.id))
                        }
                    }
                    PlayerScreen(
                        Modifier.padding(start = startPadding, end = endPadding),
                        queue,
                        playerState,
                        uiState,
                        state,
                        playlistState,
                        progress,
                        playlistProgress,
                        mainVM::onPlaybackEvent,
                        mainVM::onPlayerEvent,
                        mainVM::onUiEvent,
                    )
                    NavBar(
                        visible = isParent and (!searchState.expanded),
                        progress = progress,
                        route = currentNavRoute
                    ) {
                        navController.navigate(it) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    Indicator(pullToRefreshState, false, Modifier.align(Alignment.TopCenter))
                }
            }
        } else {
            WelcomeScreen(onPermissionRequest)
        }
    }
    bottomSheetItem?.run {
        ItemBottomSheet(
            open = uiState.bottomSheetVisible,
            state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = { mainVM.onUiEvent(UiEvent.HideBottomSheet) },
            file = this,
            onPlaybackEvent = mainVM::onPlaybackEvent,
            onUpdateFavorite = { path, favorite ->
                mainVM.onPlayerEvent(PlayerEvent.UpdateFavorite(path, favorite))
            },
            onUiEvent = mainVM::onUiEvent,
            navigateToAlbum = {
                navController.navigate(NavRoutes.Album(album))
            },
            navigateToArtist = {
                navController.navigate(NavRoutes.Artist(artist))
            },
            shareFile = {
                context.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_STREAM, contentUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            type = "audio/*"
                        },
                        "Share",
                    ),
                )
            },
        )
    }
    uiState.listBottomSheetList?.run {
        ListBottomSheet(
            open = uiState.listBottomSheetVisible,
            list = this,
            onPlaybackEvent = mainVM::onPlaybackEvent,
            onUiEvent = mainVM::onUiEvent,
            title = uiState.listBottomSheetTitle,
            text = pluralStringResource(R.plurals.item_s, size, size),
            cover = uiState.listBottomSheetImage,
            alternative = uiState.listBottomSheetIcon,
            state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shareFiles = { context.shareFiles(this) },
        )
    }
    PlaylistBottomSheet(
        open = uiState.playlistBottomSheetVisible,
        state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = { mainVM.onPlaylistsEvent(PlaylistsUiEvent.HidePlaylistBottomSheet) },
        id = playlist.id,
        title = playlist.name,
        cover = playlist.image,
        files = playlist.items,
        onPlaybackEvent = mainVM::onPlaybackEvent,
        onUiEvent = mainVM::onUiEvent,
        delete = { mainVM.onPlaylistEvent(PlaylistEvent.DeletePlaylist(playlist.toPlaylist())) },
        addToHomeScreen = {
            val manager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
            manager.requestPinShortcut(
                ShortcutInfo
                    .Builder(context, "playlist-${playlist.id}")
                    .setIcon(Icon.createWithResource(context, R.drawable.baseline_playlist_play_24))
                    .setShortLabel(playlist.name)
                    .setLongLabel(playlist.name)
                    .setIntent(
                        Intent(context, MainActivity::class.java).apply {
                            action = ACTION_VIEW
                            putExtra("type", "playlist")
                            putExtra("id", playlist.id)
                        },
                    )
                    .build(),
                null,
            )
        },
        savePlaylist = {
            mainVM.onUiEvent(UiEvent.SavePlaylist(playlist.toPlaylist()))
        },
    ) {
        context.shareFiles(playlist.items)
    }
    QueueBottomSheet(
        uiState.queueSheetVisible,
        rememberModalBottomSheetState(),
        { mainVM.onUiEvent(UiEvent.HideQueueBottomSheet) },
        { mainVM.onPlaybackEvent(PlaybackEvent.Stop) },
        {
            mainVM.onUiEvent(
                UiEvent.ShowCreatePlaylistDialog(queue.items.map { it.path })
            )
        },
        {
            mainVM.onUiEvent(
                UiEvent.ShowAddToPlaylistDialog(queue.items.map { it.path })
            )
        }
    )
    RenamePlaylistDialog(
        uiState.renamePlaylistDialogVisible,
        {
            mainVM.onUiEvent(UiEvent.HideRenamePlaylistDialog)
        },
        {
            mainVM.onPlaylistEvent(PlaylistEvent.RenamePlaylist(uiState.renamePlaylistId, uiState.renamePlaylistName))
        },
        uiState.renamePlaylistName,
        {
            mainVM.onUiEvent(UiEvent.UpdateRenamePlaylistName(it))
        },
    )
    SpeedDialog(
        uiState.speedDialog,
        { mainVM.onUiEvent(UiEvent.HideSpeedDialog) },
        playerState.speed,
        { mainVM.onPlaybackEvent(PlaybackEvent.SetSpeed(it)) },
    )
    PitchDialog(
        uiState.pitchDialog,
        { mainVM.onUiEvent(UiEvent.HidePitchDialog) },
        playerState.pitch,
        { mainVM.onPlaybackEvent(PlaybackEvent.SetPitch(it)) },
    )
    TimerDialog(
        uiState.timerDialog,
        playerState.timer,
        { mainVM.onUiEvent(UiEvent.HideTimerDialog) },
        { mainVM.onPlaybackEvent(PlaybackEvent.SetTimer(it)) },
    )
    CreatePlaylistDialog(
        uiState.newPlaylistDialog,
        uiState.newPlaylistName,
        { mainVM.onUiEvent(UiEvent.UpdateNewPlaylistName(it)) },
        uiState.newPlaylistImage,
        { mainVM.onUiEvent(UiEvent.UpdateNewPlaylistImage(it)) },
        { mainVM.onUiEvent(UiEvent.HideNewPlaylistDialog) },
        { mainVM.onPlaylistEvent(PlaylistEvent.CreateNew(uiState.newPlaylistName, uiState.newPlaylistItems, uiState.newPlaylistImage)) },
    )
    AddToPlaylistDialog(
        uiState.addToPlaylistDialog,
        playlists,
        uiState.addToPlaylistSelected,
        {
            mainVM.onUiEvent(UiEvent.UpdateSelectedPlaylist(it))
        },
        {
            mainVM.onUiEvent(UiEvent.ShowCreatePlaylistDialog(emptyList()))
        },
        {
            mainVM.onUiEvent(UiEvent.HideAddToPlaylistDialog)
        },
        {
            mainVM.onPlaylistEvent(
                PlaylistEvent.AddToPlaylist(
                    uiState.addToPlaylistSelected.map { playlists[it].id }.toSet(),
                    uiState.addToPlaylistItems
                )
            )
        }
    )
    MetadataDialog(
        uiState.metadataDialog,
        { mainVM.onUiEvent(UiEvent.HideMetadataDialog) },
        { /*mainVM.onFilesEvent(FilesEvent.UpdateMetadata(uiState.metadata))*/ },
        uiState.metadata,
        { mainVM.onUiEvent(UiEvent.UpdateMetadata(it)) },
    )
    uiState.detailsFile?.let {
        DetailsDialog(
            uiState.detailsDialog,
            { mainVM.onUiEvent(UiEvent.DismissDetails) },
            it,
        )
    }
    BackHandler(state.settledValue == ViewState.LARGE) {
        scope.launch {
            if (uiState.playlistViewState == PlaylistViewState.EXPANDED) {
                playlistState.animateTo(PlaylistViewState.COLLAPSED)
            } else {
                state.animateTo(ViewState.SMALL)
            }
        }
    }
    CollectEvents { event ->
        if (event is Event.ExpandPlayer)
            if (state.settledValue == ViewState.HIDDEN)
                scope.launch {
                    state.animateTo(ViewState.SMALL)
                }
    }
}
