package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import soup.compose.material.motion.animation.materialSharedAxisYIn
import soup.compose.material.motion.animation.materialSharedAxisYOut
import younesbouhouche.musicplayer.features.main.presentation.components.SongInfoContent
import younesbouhouche.musicplayer.features.main.presentation.routes.album.AlbumScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.album.AlbumsScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.artist.ArtistScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.artist.ArtistsScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.home.HomeScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.library.LibraryScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.playlist.PlaylistScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.playlist.PlaylistsScreen
import younesbouhouche.musicplayer.features.main.presentation.util.containerClip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
) {
    val navigationState = navigator.state
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }
    val screenModifier = Modifier.containerClip(background = Color.Transparent)
    NavDisplay(
        modifier = modifier,
        transitionSpec = {
            materialSharedAxisYIn(true, 100) togetherWith
                    materialSharedAxisYOut(false, 100)
        },
        popTransitionSpec = {
            materialSharedAxisYIn(true, 100) togetherWith
                    materialSharedAxisYOut(false, 100)
        },
        predictivePopTransitionSpec = {
            materialSharedAxisYIn(true, 100) togetherWith
                    materialSharedAxisYOut(false, 100)
        },
        sceneStrategy = bottomSheetStrategy,
        entries = navigationState.toEntries(
            entryProvider {
                entry<MainNavRoute.Home> {
                    HomeScreen(
                        bottomPadding = bottomPadding,
                        onArtistClick = { artist ->
                            navigator.navigate(MainNavRoute.Artist(artist.name))
                        },
                        modifier = screenModifier
                    ) {
                        navigator.navigate(MainNavRoute.Library)
                    }
                }
                entry<MainNavRoute.Albums> {
                    AlbumsScreen(
                        bottomPadding = bottomPadding,
                        modifier = screenModifier
                    ) { album ->
                        navigator.navigate(MainNavRoute.Album(album.name))
                    }
                }
                entry<MainNavRoute.Album> {
                    AlbumScreen(
                        it.name,
                        bottomPadding = bottomPadding
                    ) {
                        navigator.navigate(MainNavRoute.SongInfo(it.id))
                    }
                }
                entry<MainNavRoute.Artists> {
                    ArtistsScreen(
                        bottomPadding = bottomPadding,
                        modifier = screenModifier
                    ) { album ->
                        navigator.navigate(MainNavRoute.Artist(album.name))
                    }
                }
                entry<MainNavRoute.Artist> {
                    ArtistScreen(
                        it.name,
                        bottomPadding = bottomPadding
                    ) {
                        navigator.navigate(MainNavRoute.SongInfo(it.id))
                    }
                }
                entry<MainNavRoute.Playlists> {
                    PlaylistsScreen(
                        bottomPadding = bottomPadding,
                        modifier = screenModifier
                    ) { playlist ->
                        navigator.navigate(MainNavRoute.Playlist(playlist.id))
                    }
                }
                entry<MainNavRoute.Playlist> {
                    PlaylistScreen(
                        it.id,
                        bottomPadding = bottomPadding
                    ) {
                        navigator.navigate(MainNavRoute.SongInfo(it.id))
                    }
                }
                entry<MainNavRoute.Library> {
                    LibraryScreen(
                        bottomPadding = bottomPadding,
                        modifier = screenModifier,
                    ) {
                        navigator.navigate(MainNavRoute.SongInfo(it))
                    }
                }
                entry<MainNavRoute.SongInfo> (
                    metadata = BottomSheetSceneStrategy.bottomSheet()
                ) {
                    SongInfoContent(it.songId)
                }
            }
        ),
        onBack = navigator::goBack,
    )
}