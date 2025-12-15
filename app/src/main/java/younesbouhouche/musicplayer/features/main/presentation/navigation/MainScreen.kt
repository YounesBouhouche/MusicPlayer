package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.features.main.presentation.player.NavigationWithPlayer
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.player.domain.models.PlayState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navigateToSettings: () -> Unit,
) {
    val mainVM = koinViewModel<MainViewModel>()
    val navigationState = rememberNavigationState(
        startRoute = TopLevelRoutes.Home.destination,
        topLevelRoutes = TopLevelRoutes.entries.map { it.destination }.toSet(),
        serializersConfig = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(MainNavRoute.Home::class, MainNavRoute.Home.serializer())
                    subclass(MainNavRoute.Albums::class, MainNavRoute.Albums.serializer())
                    subclass(MainNavRoute.Artists::class, MainNavRoute.Artists.serializer())
                    subclass(MainNavRoute.Album::class, MainNavRoute.Album.serializer())
                    subclass(MainNavRoute.Artist::class, MainNavRoute.Artist.serializer())
                    subclass(MainNavRoute.Playlists::class, MainNavRoute.Playlists.serializer())
                    subclass(MainNavRoute.Playlist::class, MainNavRoute.Playlist.serializer())
                    subclass(MainNavRoute.Library::class, MainNavRoute.Library.serializer())
                    subclass(MainNavRoute.SongInfo::class, MainNavRoute.SongInfo.serializer())
                }
            }
        }
    )
    val navigator = remember {
        Navigator(navigationState)
    }
    val loadingState by mainVM.loadingState.collectAsStateWithLifecycle()
    val playerState by mainVM.playerState.collectAsStateWithLifecycle()
    val currentRoute = navigationState.backStacks[navigationState.topLevelRoute]?.last()
    val currentNavRoute = TopLevelRoutes.entries.firstOrNull { routes ->
        routes.destination == navigationState.topLevelRoute
    }
    val isParent = (currentRoute in TopLevelRoutes.entries.map { it.destination }) or (currentRoute is MainNavRoute.SongInfo)
    var viewHeight by remember { mutableIntStateOf(0) }
    val bottomPadding =
        WindowInsets.navigationBars.add(
            WindowInsets(bottom = 100.dp +
                (if (playerState.playState != PlayState.STOP) 100.dp else 0.dp)
            )
        ).asPaddingValues().calculateBottomPadding()
    Surface(modifier.fillMaxSize()) {
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
                            0.0f to MaterialTheme.colorScheme.tertiaryContainer,
                            .4f to MaterialTheme.colorScheme.surface,
                        )
                    )
            ) {
                AnimatedVisibility(isParent) {
                    AnimatedContent(
                        targetState = loadingState.isLoading(),
                        transitionSpec = {
                            if (initialState) {
                                slideInVertically { height -> height } + fadeIn() togetherWith
                                        slideOutVertically { height -> -height } + fadeOut()
                            } else {
                                slideInVertically { height -> -height } + fadeIn() togetherWith
                                        slideOutVertically { height -> height } + fadeOut()
                            }
                        }
                    ) {
                        if (it)
                            LoadingBar(loadingState)
                        else
                            SearchScreen(
                                onShowBottomSheet = {
                                    navigator.navigate(MainNavRoute.SongInfo(it.id))
                                },
                                onAlbumClick = {
                                    navigator.navigate(MainNavRoute.Album(it.name))
                                },
                                onArtistClick = {
                                    navigator.navigate(MainNavRoute.Artist(it.name))
                                },
                                onPlaylistClick = {
                                    navigator.navigate(MainNavRoute.Playlist(it.id))
                                },
                                navigateToSettings = navigateToSettings
                            )
                    }
                }
                MainNavGraph(
                    navigator,
                    Modifier.fillMaxSize(),
                    bottomPadding
                )
            }
            NavigationWithPlayer(
                viewHeight,
                currentNavRoute,
            ) {
                navigator.navigate(it)
            }
        }
//        CreatePlaylistDialog(
//            visible = uiState.newPlaylistDialog,
//            playlistName = uiState.newPlaylistName,
//            onNameChange = {
//                mainVM.onUiEvent(UiEvent.UpdateNewPlaylistName(it))
//                           },
//            image = uiState.newPlaylistImage,
//            onImageChange = {
//                mainVM.onUiEvent(UiEvent.UpdateNewPlaylistImage(it))
//            },
//            onDismissRequest = {
//                mainVM.onUiEvent(UiEvent.HideNewPlaylistDialog)
//            },
//        ) {
//            mainVM.onPlaylistEvent(
//                PlaylistEvent.CreateNew(
//                    uiState.newPlaylistName,
//                    emptyList(),
//                    uiState.newPlaylistImage
//                )
//            )
//            mainVM.onUiEvent(UiEvent.HideNewPlaylistDialog)
//        }
//        AddToPlaylistDialog(
//            uiState.addToPlaylistDialog,
//            playlists,
//            uiState.addToPlaylistSelected,
//            {
//                mainVM.onUiEvent(UiEvent.UpdateSelectedPlaylist(it))
//            },
//            {
//                mainVM.onUiEvent(UiEvent.ShowCreatePlaylistDialog(emptyList()))
//            },
//            {
//                mainVM.onUiEvent(UiEvent.HideAddToPlaylistDialog)
//            }
//        ) {
//            mainVM.onPlaylistEvent(
//                PlaylistEvent.AddToPlaylist(
//                    uiState.addToPlaylistSelected,
//                    uiState.addToPlaylistItems
//                )
//            )
//            mainVM.onUiEvent(UiEvent.HideAddToPlaylistDialog)
//        }
    }
}