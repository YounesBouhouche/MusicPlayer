package younesbouhouche.musicplayer.features.main.presentation.layout

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.features.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.features.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.features.main.domain.events.UiEvent
import younesbouhouche.musicplayer.features.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.features.main.domain.models.Routes
import younesbouhouche.musicplayer.features.main.presentation.components.DrawerSheetContent
import younesbouhouche.musicplayer.features.main.presentation.components.MusicCardBottomSheet
import younesbouhouche.musicplayer.features.main.presentation.dialogs.AddToPlaylistDialog
import younesbouhouche.musicplayer.features.main.presentation.dialogs.CreatePlaylistDialog
import younesbouhouche.musicplayer.features.main.presentation.player.NavigationWithPlayer
import younesbouhouche.musicplayer.features.main.presentation.states.PlayState
import younesbouhouche.musicplayer.features.main.presentation.util.containerClip
import younesbouhouche.musicplayer.features.main.presentation.util.intUpDownTransSpec
import younesbouhouche.musicplayer.features.main.presentation.util.isRouteParent
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.SearchVM
import younesbouhouche.musicplayer.features.main.util.navigateTo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainNavGraph(
    mainVM: MainViewModel,
    modifier: Modifier = Modifier,
    navigateToSettings: () -> Unit,
) {
    val navController = rememberNavController()
    val searchVM = koinViewModel<SearchVM>()
    val searchState by searchVM.searchState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isParent = currentRoute.isRouteParent
    var bottomSheetFile by remember { mutableStateOf<MusicCard?>(null) }
    var viewHeight by remember { mutableIntStateOf(0) }
    val playlists by mainVM.playlists.collectAsState()
    val queue by mainVM.queue.collectAsState()
    val playerState by mainVM.playerState.collectAsState()
    val padding by animateDpAsState(if (isParent) 8.dp else 0.dp)
    val uiState by mainVM.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val currentNavRoute =
        currentRoute?.let { route ->
            Routes
                .entries
                .firstOrNull {
                    it.destination.javaClass.kotlin.qualifiedName?.contains(route) == true
                }
            }
    Surface(modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerSheetContent(
                    drawerState,
                    queue.getCurrentItem()
                        ?.takeIf { playerState.playState != PlayState.STOP }
                )
            }
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        viewHeight = it.size.height
                    }
                    .onSizeChanged {
                        viewHeight = it.height
                    }
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.0f to MaterialTheme.colorScheme.surfaceContainerLow,
                                1.0f to MaterialTheme.colorScheme.surface,
                            )
                        )
                ) {
                    AnimatedVisibility(isParent) {
                        SearchScreen(
                            searchState = searchState,
                            onExpandDrawer = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            onAction = searchVM::onSearchEvent,
                            onShowBottomSheet = {
                                bottomSheetFile = it
                            },
                            onAlbumClick = {
                                navController.navigateTo(NavRoutes.Album(it.name))
                            },
                            onArtistClick = {
                                navController.navigateTo(NavRoutes.Artist(it.name))
                            },
                            onPlaylistClick = {
                                navController.navigateTo(NavRoutes.Playlist(it.id))
                            },
                            navigateToSettings = navigateToSettings,
                            onPlay = {
                                mainVM.onPlaybackEvent(
                                    PlaybackEvent.Play(
                                        searchState.result.files,
                                        it,
                                        shuffle = false
                                    )
                                )
                            },
                        )
                    }
                    AnimatedVisibility(
                        uiState.loading.isLoading() and isParent,
                        enter = expandVertically(expandFrom = Alignment.Top),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top),
                    ) {
                        Row(Modifier
                            .padding(8.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(16.dp)
                            .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularWavyProgressIndicator(
                                progress = {
                                    uiState.loading.getValue()
                                },
                                Modifier.size(40.dp),
                                stroke =
                                    Stroke(
                                        width = with(LocalDensity.current) { 3.dp.toPx() },
                                        cap = StrokeCap.Round,
                                    ),
                                trackStroke =
                                    Stroke(
                                        width = with(LocalDensity.current) { 3.dp.toPx() },
                                        cap = StrokeCap.Round,
                                    ),
                            )
                            AnimatedContent(
                                uiState.loading.step,
                                transitionSpec = intUpDownTransSpec,
                            ) { step ->
                                Text(
                                    stringResource(
                                        when(step) {
                                            0 -> R.string.loading_files
                                            1 -> R.string.loading_thumbnails
                                            2 -> R.string.loading_artists
                                            else -> R.string.loading
                                        },
                                        uiState.loading.progress,
                                        uiState.loading.progressMax,
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    NavigationHost(
                        navController,
                        mainVM,
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = padding)
                    ) {
                        bottomSheetFile = it
                    }
                }
                NavigationWithPlayer(
                    viewHeight,
                    queue,
                    playerState,
                    mainVM::onPlayerEvent,
                    mainVM::onPlaybackEvent,
                    mainVM::onUiEvent,
                    currentNavRoute,
                ) {
                    navController.navigateTo(it)
                }
            }
        }
        MusicCardBottomSheet(
            bottomSheetFile,
            {
                bottomSheetFile = null
            },
            {
                bottomSheetFile?.let {
                    mainVM.onPlaybackEvent(PlaybackEvent.AddToQueue(listOf(it)))
                }
            },
            {
                bottomSheetFile?.let {
                    mainVM.onUiEvent(
                        UiEvent.ShowAddToPlaylistDialog(listOf(it.path))
                    )
                }
            },
            onToggleFavorite = {
                bottomSheetFile?.let {
                    bottomSheetFile = it.copy(favorite = !it.favorite)
                    mainVM.onPlayerEvent(PlayerEvent.UpdateFavorite(it.path, !it.favorite))
                }
            },
            onShare = {
                bottomSheetFile?.let {
                    context.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_STREAM, it.contentUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                type = "audio/*"
                            },
                            "Share",
                        ),
                    )
                }
            }
        ) {
            bottomSheetFile?.let {
                mainVM.onPlaybackEvent(PlaybackEvent.Play(listOf(it)))
            }
        }
        CreatePlaylistDialog(
            visible = uiState.newPlaylistDialog,
            playlistName = uiState.newPlaylistName,
            onNameChange = {
                mainVM.onUiEvent(UiEvent.UpdateNewPlaylistName(it))
                           },
            image = uiState.newPlaylistImage,
            onImageChange = {
                mainVM.onUiEvent(UiEvent.UpdateNewPlaylistImage(it))
            },
            onDismissRequest = {
                mainVM.onUiEvent(UiEvent.HideNewPlaylistDialog)
            },
        ) {
            mainVM.onPlaylistEvent(
                PlaylistEvent.CreateNew(
                    uiState.newPlaylistName,
                    emptyList(),
                    uiState.newPlaylistImage
                )
            )
            mainVM.onUiEvent(UiEvent.HideNewPlaylistDialog)
        }
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
            }
        ) {
            mainVM.onPlaylistEvent(
                PlaylistEvent.AddToPlaylist(
                    uiState.addToPlaylistSelected,
                    uiState.addToPlaylistItems
                )
            )
            mainVM.onUiEvent(UiEvent.HideAddToPlaylistDialog)
        }
    }
}