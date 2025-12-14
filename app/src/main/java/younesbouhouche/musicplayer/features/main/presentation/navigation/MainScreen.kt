package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.presentation.player.NavigationWithPlayer
import younesbouhouche.musicplayer.features.main.presentation.util.intUpDownTransSpec
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.player.domain.models.PlayState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navigateToSettings: () -> Unit,
) {
    val backStack = rememberNavBackStack(configuration = SavedStateConfiguration {
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
    }, MainNavRoute.Home)
    val mainVM = koinViewModel<MainViewModel>()
    val loadingState by mainVM.loadingState.collectAsStateWithLifecycle()
    val playerState by mainVM.playerState.collectAsStateWithLifecycle()
    val queue by mainVM.queue.collectAsState()
    val currentRoute = backStack.last() as MainNavRoute
    val currentNavRoute = Routes.entries.firstOrNull { routes -> routes.destination == currentRoute }
    val isParent = (currentRoute in Routes.entries.map { it.destination }) or (currentRoute is MainNavRoute.SongInfo)
    var viewHeight by remember { mutableIntStateOf(0) }
    val padding by animateDpAsState(if (isParent) 8.dp else 0.dp)
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
                            0.0f to MaterialTheme.colorScheme.surfaceContainerLow,
                            1.0f to MaterialTheme.colorScheme.surface,
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
                                    backStack.add(MainNavRoute.SongInfo(it.id))
                                },
                                onAlbumClick = {
                                    backStack.add(MainNavRoute.Album(it.name))
                                },
                                onArtistClick = {
                                    backStack.add(MainNavRoute.Artist(it.name))
                                },
                                onPlaylistClick = {
                                    backStack.add(MainNavRoute.Playlist(it.id))
                                },
                                navigateToSettings = navigateToSettings
                            )
                    }
                }
                MainNavGraph(
                    backStack,
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = padding),
                    bottomPadding
                )
            }
            NavigationWithPlayer(
                viewHeight,
                currentNavRoute,
            ) {
                backStack.add(it)
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