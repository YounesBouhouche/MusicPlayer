package younesbouhouche.musicplayer.features.main.presentation.layout

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import soup.compose.material.motion.animation.materialSharedAxisYIn
import soup.compose.material.motion.animation.materialSharedAxisYOut
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.features.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.features.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.features.main.domain.events.UiEvent
import younesbouhouche.musicplayer.features.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.features.main.presentation.routes.AlbumScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.AlbumsScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.ArtistScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.ArtistsScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.HomeScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.LibraryScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.PlaylistScreen
import younesbouhouche.musicplayer.features.main.presentation.routes.PlaylistsScreen
import younesbouhouche.musicplayer.features.main.presentation.states.PlayState
import younesbouhouche.musicplayer.features.main.presentation.util.Event
import younesbouhouche.musicplayer.features.main.presentation.util.containerClip
import younesbouhouche.musicplayer.features.main.presentation.util.sendEvent
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.main.util.navigateTo

@Composable
fun NavigationHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onShowBottomSheet: (MusicCard) -> Unit
) {
    val files by viewModel.files.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val lastAdded by viewModel.lastAdded.collectAsState()
    val mostPlayedArtists by viewModel.mostPlayedArtists.collectAsState()
    val history by viewModel.history.collectAsState()
    val albumsSortState by viewModel.albumsSortState.collectAsState()
    val artistsSortState by viewModel.artistsSortState.collectAsState()
    val playlistsSortState by viewModel.playlistsSortState.collectAsState()
    val listScreenSortState by viewModel.listScreenSortState.collectAsState()
    val playlistSortState by viewModel.playlistSortState.collectAsState()
    val librarySortState by viewModel.sortState.collectAsState()
    val playlist by viewModel.playlist.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val bottomPadding by animateDpAsState(
        if (playerState.playState != PlayState.STOP) 200.dp else 110.dp
    )
    NavHost(
        navController,
        NavRoutes.Home,
        modifier,
        enterTransition = {
            materialSharedAxisYIn(true, 100)
        },
        exitTransition = {
            materialSharedAxisYOut(false, 100)
        }
    ) {
        composable<NavRoutes.Home> {
            HomeScreen(
                mostPlayedArtists,
                lastAdded,
                favorites,
                history,
                bottomPadding = bottomPadding,
                onArtistClick = { artist ->
                    navController.navigateTo(NavRoutes.Artist(artist.name))
                },
                modifier = Modifier.containerClip()
            ) { list, index ->
                viewModel.onPlaybackEvent(PlaybackEvent.Play(list, index))
            }
        }
        navigation<NavRoutes.AlbumsRoute>(NavRoutes.Albums) {
            composable<NavRoutes.Albums> {
                AlbumsScreen(
                    albums,
                    albumsSortState,
                    viewModel::onAlbumsSortChange,
                    bottomPadding = bottomPadding,
                    modifier = Modifier.containerClip()
                ) { album ->
                    navController.navigateTo(NavRoutes.Album(album.name))
                }
            }
            composable<NavRoutes.Album> { entry ->
                val route = entry.toRoute<NavRoutes.Album>()
                val album by viewModel.getAlbumUi(route.title).collectAsState()
                AlbumScreen(
                    album,
                    listScreenSortState,
                    viewModel::onListScreenSortChange,
                    onShowBottomSheet = onShowBottomSheet,
                    bottomPadding = bottomPadding
                ) { items, index, shuffle ->
                    viewModel.onPlaybackEvent(PlaybackEvent.Play(
                        items,
                        index,
                        shuffle = shuffle
                    ))
                }
            }
        }
        navigation<NavRoutes.ArtistsRoute>(NavRoutes.Artists) {
            composable<NavRoutes.Artists> {
                ArtistsScreen(
                    artists,
                    artistsSortState,
                    viewModel::onArtistsSortChange,
                    modifier = Modifier.containerClip()
                ) { artist ->
                    navController.navigateTo(NavRoutes.Artist(artist.name))
                }
            }
            composable<NavRoutes.Artist> { entry ->
                val route = entry.toRoute<NavRoutes.Artist>()
                val artist by viewModel.getArtistUi(route.name).collectAsState()
                ArtistScreen(
                    artist,
                    listScreenSortState,
                    viewModel::onListScreenSortChange,
                    onShowBottomSheet = onShowBottomSheet
                ) { items, index, shuffle ->
                    viewModel.onPlaybackEvent(
                        PlaybackEvent.Play(
                            items,
                            index,
                            shuffle = shuffle
                        )
                    )
                }
            }
        }
        navigation<NavRoutes.PlaylistsRoute>(NavRoutes.Playlists) {
            composable<NavRoutes.Playlists> {
                PlaylistsScreen(
                    playlists,
                    playlistsSortState,
                    viewModel::onPlaylistsSortChange,
                    bottomPadding = bottomPadding,
                    modifier = Modifier.containerClip(),
                    onCreatePlaylist = {
                        viewModel.onUiEvent(UiEvent.ShowCreatePlaylistDialog(emptyList()))
                    },
                    onImportPlaylist = {
                        viewModel.sendEvent(Event.LaunchPlaylistDialog)
                    },
                    onPlay = {
                        viewModel.onPlayerEvent(PlayerEvent.PlayPlaylist(it.id))
                    }
                ) { playlist ->
                    navController.navigateTo(NavRoutes.Playlist(playlist.id))
                }
            }
            composable<NavRoutes.Playlist> { entry ->
                val route = entry.toRoute<NavRoutes.Playlist>()
                LaunchedEffect(route) {
                    viewModel.getPlaylist(route.playlistId)
                }
                PlaylistScreen(
                    playlist,
                    playlistSortState,
                    viewModel::onPlaylistSortChange,
                    bottomPadding = bottomPadding,
                    onShowBottomSheet = onShowBottomSheet,
                    onReorder = { from, to ->
                        viewModel.onPlaylistEvent(PlaylistEvent.Reorder(playlist, from, to))
                    },
                    onRemove = {
                        viewModel.onPlaylistEvent(PlaylistEvent.RemoveAt(playlist, it))
                    }
                ) { items, index, shuffle ->
                    viewModel.onPlaybackEvent(
                        PlaybackEvent.Play(
                            items,
                            index,
                            shuffle = shuffle
                        )
                    )
                }
            }
        }
        composable<NavRoutes.Library> {
            LibraryScreen(
                files,
                librarySortState,
                viewModel::onLibrarySortChange,
                bottomPadding = bottomPadding,
                modifier = Modifier.containerClip(),
                onShowBottomSheet = onShowBottomSheet
            ) { file ->
                viewModel.onPlaybackEvent(PlaybackEvent.Play(listOf(file)))
            }
        }
    }
}