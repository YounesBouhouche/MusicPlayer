package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
                    subclass(MainNavRoute.CreatePlaylist::class, MainNavRoute.CreatePlaylist.serializer())
                    subclass(MainNavRoute.AddToPlaylist::class, MainNavRoute.AddToPlaylist.serializer())
                    subclass(MainNavRoute.Library::class, MainNavRoute.Library.serializer())
                    subclass(MainNavRoute.SongInfo::class, MainNavRoute.SongInfo.serializer())
                    subclass(MainNavRoute.MetadataEditor::class, MainNavRoute.MetadataEditor.serializer())
                }
            }
        }
    )
    val navigator = remember {
        Navigator(navigationState)
    }
    val isLoading by mainVM.isLoading.collectAsStateWithLifecycle()
    val playerState by mainVM.playerState.collectAsStateWithLifecycle()
    val currentRoute = navigationState.backStacks[navigationState.topLevelRoute]?.let {
        it.lastOrNull { navKey -> (navKey as? MainNavRoute)?.isDialog == false }
            ?: it.lastOrNull()
    }
    val currentNavRoute = TopLevelRoutes.entries.firstOrNull { routes ->
        routes.destination == navigationState.topLevelRoute
    }
    val isParent = (currentRoute in TopLevelRoutes.entries.map { it.destination })
    var viewHeight by remember { mutableIntStateOf(0) }
    val bottomPadding =
        WindowInsets.navigationBars.add(
            WindowInsets(bottom = 100.dp +
                (if (playerState.playState != PlayState.STOP) 80.dp else 0.dp)
            )
        ).asPaddingValues().calculateBottomPadding()
    Surface(modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = {
                mainVM.refreshLibrary()
            },
            modifier = Modifier
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
                            0.0f to MaterialTheme.colorScheme.tertiaryContainer
                                .copy(alpha = 0.5f),
                            .4f to MaterialTheme.colorScheme.surface,
                        )
                    )
            ) {
                AnimatedVisibility(isParent) {
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
                Box(Modifier.fillMaxWidth()) {
                    MainNavGraph(
                        navigator,
                        Modifier.fillMaxSize(),
                        bottomPadding
                    )
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isLoading,
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                    ) {
                        LinearWavyProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            waveSpeed = (-40).dp,
                            wavelength = 60.dp,
                        )
                    }
                }
            }
            NavigationWithPlayer(
                viewHeight,
                currentNavRoute,
            ) {
                navigator.navigate(it)
            }
        }
    }
}