package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
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
import younesbouhouche.musicplayer.features.main.presentation.routes.playlist.AddToPlaylistContent
import younesbouhouche.musicplayer.features.main.presentation.routes.playlist.CreatePlaylistContent
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
    val sceneStrategy = remember { SceneStrategy<NavKey>() }
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
        sceneStrategy = sceneStrategy,
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
                        modifier = screenModifier,
                        onCreatePlaylist = {
                            navigator.navigate(MainNavRoute.CreatePlaylist)
                        }
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
                entry<MainNavRoute.CreatePlaylist> (
                    metadata = SceneStrategy.dialog()
                ) {
                    CreatePlaylistContent {
                        navigator.goBack()
                    }
                }
                entry<MainNavRoute.AddToPlaylist> (
                    metadata = SceneStrategy.bottomSheet()
                ) {
                    AddToPlaylistContent(
                        it.ids,
                        { navigator.navigate(MainNavRoute.CreatePlaylist) }
                    ) {
                        navigator.goBack()
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
                    metadata = SceneStrategy.bottomSheet()
                ) {
                    SongInfoContent(it.songId) {
                        navigator.navigate(MainNavRoute.AddToPlaylist(listOf(it.songId)))
                    }
                }
            }
        ),
        onBack = navigator::goBack,
    )
}