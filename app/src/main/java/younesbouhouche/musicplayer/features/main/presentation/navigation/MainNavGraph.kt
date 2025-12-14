package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
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
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
) {
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeAt(backStack.lastIndex) },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator {
                false
            }
        ),
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
    ) { key ->
        when(key) {
            is MainNavRoute.Home -> {
                NavEntry(key) {
                    HomeScreen(
                        bottomPadding = bottomPadding,
                        onArtistClick = { artist ->
                            backStack.add(MainNavRoute.Artist(artist.name))
                        },
                        modifier = Modifier.containerClip()
                    )
                }
            }
            is MainNavRoute.Albums -> {
                NavEntry(key) {
                    AlbumsScreen(
                        bottomPadding = bottomPadding,
                        modifier = Modifier.containerClip()
                    ) { album ->
                        backStack.add(MainNavRoute.Album(album.name))
                    }
                }
            }
            is MainNavRoute.Album -> {
                NavEntry(key) {
                    AlbumScreen(
                        key.name,
                        bottomPadding = bottomPadding
                    ) {
                        backStack.add(MainNavRoute.SongInfo(it.id))
                    }
                }
            }
            is MainNavRoute.Artists -> {
                NavEntry(key) {
                    ArtistsScreen(
                        bottomPadding = bottomPadding,
                        modifier = Modifier.containerClip()
                    ) { album ->
                        backStack.add(MainNavRoute.Artist(album.name))
                    }
                }
            }
            is MainNavRoute.Artist -> {
                NavEntry(key) {
                    ArtistScreen(
                        key.name,
                        bottomPadding = bottomPadding
                    ) {
                        backStack.add(MainNavRoute.SongInfo(it.id))
                    }
                }
            }
            is MainNavRoute.Playlists -> {
                NavEntry(key) {
                    PlaylistsScreen(
                        bottomPadding = bottomPadding,
                        modifier = Modifier.containerClip()
                    ) { playlist ->
                        backStack.add(MainNavRoute.Playlist(playlist.id))
                    }
                }
            }
            is MainNavRoute.Playlist -> {
                NavEntry(key) {
                    PlaylistScreen(
                        key.id,
                        bottomPadding = bottomPadding
                    ) {
                        backStack.add(MainNavRoute.SongInfo(it.id))
                    }
                }
            }
            is MainNavRoute.Library -> {
                NavEntry(key) {
                    LibraryScreen(
                        bottomPadding = bottomPadding,
                        modifier = Modifier.containerClip(),
                    ) {
                        backStack.add(MainNavRoute.SongInfo(it))
                    }
                }
            }
            is MainNavRoute.SongInfo -> {
                NavEntry(
                    key = key,
                    metadata = BottomSheetSceneStrategy.bottomSheet()
                ) {
                    SongInfoContent(key.songId)
                }
            }
            else -> error("")
        }
    }
}