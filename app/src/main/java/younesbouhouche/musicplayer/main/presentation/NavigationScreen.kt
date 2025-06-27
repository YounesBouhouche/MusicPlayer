package younesbouhouche.musicplayer.main.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.koin.compose.viewmodel.koinViewModel
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistsUiEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.main.presentation.routes.Albums
import younesbouhouche.musicplayer.main.presentation.routes.Artists
import younesbouhouche.musicplayer.main.presentation.routes.FavoritesScreen
import younesbouhouche.musicplayer.main.presentation.routes.Home
import younesbouhouche.musicplayer.main.presentation.routes.Library
import younesbouhouche.musicplayer.main.presentation.routes.ListScreen
import younesbouhouche.musicplayer.main.presentation.routes.PlaylistScreen
import younesbouhouche.musicplayer.main.presentation.routes.Playlists
import younesbouhouche.musicplayer.main.presentation.viewmodel.FavoritesViewModel
import younesbouhouche.musicplayer.main.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(
    navController: NavHostController,
    mainVM: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val favoritesViewModel = koinViewModel<FavoritesViewModel>()
    val files by mainVM.files.collectAsState()
    val lastAdded by mainVM.lastAdded.collectAsState()
    val recentlyAdded = if (lastAdded.size > 5) lastAdded.subList(0, 5) else lastAdded
    val sortState by mainVM.sortState.collectAsState()
    val mostPlayedArtists by mainVM.mostPlayedArtists.collectAsState()

    val albums by mainVM.albums.collectAsState()
    val albumsSortState by mainVM.albumsSortState.collectAsState()

    val artists by mainVM.artists.collectAsState()
    val artistsSortState by mainVM.artistsSortState.collectAsState()

    val playlists by mainVM.playlists.collectAsState()
    val playlistsSortState by mainVM.playlistsSortState.collectAsState()
    val playlistSortState by mainVM.playlistSortState.collectAsState()

    val history by mainVM.history.collectAsState()
    val mostPlayed by mainVM.mostPlayed.collectAsState()
    val mostFivePlayed = if (mostPlayed.size > 5) mostPlayed.subList(0, 5) else mostPlayed

    val listScreenSortState by mainVM.listScreenSortState.collectAsState()

    val favorites by mainVM.favorites.collectAsState()

    SharedTransitionLayout(modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home,
            enterTransition = { materialFadeThroughIn() },
            exitTransition = { materialFadeThroughOut() },
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<NavRoutes.Home> {
                Home(
                    navigate = navController::navigate,
                    recentlyAdded = recentlyAdded,
                    mostPlayed = mostFivePlayed,
                    play = { list, index -> mainVM.onPlaybackEvent(PlaybackEvent.Play(list, index)) },
                    showInfo = { mainVM.onUiEvent(UiEvent.ShowBottomSheet(it.id)) },
                    navigateToArtist = {
                        navController.navigate(NavRoutes.Artist(it.name))
                    },
                    showArtistBottomSheet = {
                        mainVM.onUiEvent(
                            UiEvent.ShowListBottomSheet(
                                it.items,
                                it.name,
                                it.getPicture(),
                                Icons.Default.Person,
                            ),
                        )
                    },
                    artists = mostPlayedArtists,
                )
            }
            composable<NavRoutes.Albums> {
                Albums(
                    albums = albums,
                    onClick = {
                        navController.navigate(NavRoutes.Album(it))
                    },
                    onLongClick = {
                        mainVM.onUiEvent(
                            UiEvent.ShowListBottomSheet(
                                it.items,
                                it.name,
                                it.cover,
                                Icons.Default.Album,
                            ),
                        )
                    },
                    animatedContentScope = this,
                    sortState = albumsSortState,
                    onSortStateChange = mainVM::onAlbumsSortChange,
                    onPlayerEvent = mainVM::onPlayerEvent,
                )
            }
            composable<NavRoutes.Album> { entry ->
                val route = entry.toRoute<NavRoutes.Album>()
                val album by mainVM.getAlbumUi(route.title).collectAsState()
                ListScreen(
                    files = album.items,
                    title = album.name,
                    sortState = listScreenSortState,
                    onSortStateChange = mainVM::onListScreenSortChange,
                    navigateUp = navController::navigateUp,
                    onPlay = { index, shuffle ->
                        mainVM.onPlaybackEvent(PlaybackEvent.Play(album.items, index))
                    },
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(album.items[it].id))
                    },
                    animatedContentScope = this,
                    key = "album-${album.name}",
                    cover = album.cover,
                    icon = Icons.Default.Album
                )
            }
            composable<NavRoutes.Artists> {
                Artists(
                    artists = artists,
                    onClick = {
                        navController.navigate(NavRoutes.Artist(it))
                    },
                    onLongClick = {
                        mainVM.onUiEvent(
                            UiEvent.ShowListBottomSheet(
                                it.items,
                                it.name,
                                it.getPicture(),
                                Icons.Default.AccountCircle,
                            ),
                        )
                    },
                    animatedContentScope = this,
                    modifier = Modifier,
                    sortState = artistsSortState,
                    onSortStateChange = mainVM::onArtistsSortChange,
                    onPlayerEvent = mainVM::onPlayerEvent,
                )
            }
            composable<NavRoutes.Artist> { entry ->
                val route = entry.toRoute<NavRoutes.Artist>()
                val artist by mainVM.getArtistUi(route.name).collectAsState()
                ListScreen(
                    files = artist.items,
                    title = artist.name,
                    sortState = listScreenSortState,
                    onSortStateChange = mainVM::onListScreenSortChange,
                    navigateUp = navController::navigateUp,
                    onPlay = { index, shuffle ->
                        mainVM.onPlaybackEvent(PlaybackEvent.Play(artist.items, index))
                    },
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(artist.items[it].id))
                    },
                    animatedContentScope = this,
                    key = "artist-${artist.name}",
                    cover = artist.getPicture(),
                    icon = Icons.Default.Person
                )
            }
            composable<NavRoutes.Playlists> {
                Playlists(
                    playlists = playlists,
                    onClick = {
                        navController.navigate(NavRoutes.Playlist(it))
                    },
                    onLongClick = {
                        mainVM.onPlaylistsEvent(PlaylistsUiEvent.ShowBottomSheet(it))
                                  },
                    sortState = playlistsSortState,
                    onSortStateChange = {
                        mainVM.onPlaylistsEvent(PlaylistsUiEvent.SetSortState(it))
                    },
                    onPlayerEvent = mainVM::onPlayerEvent,
                    onPlaylistEvent = mainVM::onPlaylistEvent,
                    onUiEvent = mainVM::onUiEvent,
                    animatedContentScope = this
                )
            }
            composable<NavRoutes.Library> {
                Library(
                    files,
                    Modifier.testTag("library_list"),
                    sortState,
                    mainVM::onLibrarySortChange,
                    mainVM::onUiEvent,
                ) {
                    mainVM.onPlaybackEvent(PlaybackEvent.Play(files, it))
                }
            }
            composable<NavRoutes.Playlist> { entry ->
                val route = entry.toRoute<NavRoutes.Playlist>()
                LaunchedEffect(route.playlistId) {
                    mainVM.onUiEvent(UiEvent.SetPlaylist(route.playlistId))
                }
                val playlist by mainVM.playlist.collectAsState()
                PlaylistScreen(
                    playlist = playlist,
                    sortState = playlistSortState,
                    onSortStateChange = mainVM::onPlaylistSortChange,
                    navigateUp = navController::navigateUp,
                    animatedContentScope = this,
                    onRename = {
                        mainVM.onUiEvent(UiEvent.ShowRenamePlaylistDialog(playlist.id, playlist.name))
                    },
                    onSetFavorite = {
                        mainVM.onPlaylistEvent(PlaylistEvent.SetFavorite(playlist.id, it))
                    },
                    onShare = {
                        mainVM.onUiEvent(UiEvent.SharePlaylist(playlist.toPlaylist()))
                    },
                    reorder = { from, to ->
                        mainVM.onPlaylistEvent(PlaylistEvent.Reorder(playlist, from, to))
                    },
                    onDismiss = {
                        mainVM.onPlaylistEvent(PlaylistEvent.RemoveAt(playlist, it))
                    },
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(playlist.items[it].id))
                    },
                    onPlay = { index, shuffle ->
                        mainVM.onPlaybackEvent(PlaybackEvent.Play(playlist.items, index))
                    }
                )
            }
            composable<NavRoutes.Favorites> {
                FavoritesScreen(
                    favoritesViewModel,
                    onBack = {
                        navController.navigateUp()
                    },
                    onPlaylistClick = {
                        navController.navigate(NavRoutes.Playlist(it))
                    },
                    onPlayPlaylist = {
                        mainVM.onPlayerEvent(PlayerEvent.PlayPlaylist(it))
                    }
                ) { list, index ->
                    mainVM.onPlaybackEvent(PlaybackEvent.Play(list, index))
                }
            }
            composable<NavRoutes.MostPlayedScreen> {
                ListScreen(
                    files = mostFivePlayed,
                    title = stringResource(R.string.most_played),
                    sortState = null,
                    onSortStateChange = null,
                    navigateUp = navController::navigateUp,
                    onPlay = { index, shuffle ->
                        mainVM.onPlaybackEvent(PlaybackEvent.Play(mostFivePlayed, index))
                    },
                    animatedContentScope = this,
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(mostFivePlayed[it].id))
                    },
                )
            }
            composable<NavRoutes.HistoryScreen> {
                ListScreen(
                    files = history,
                    title = stringResource(R.string.history),
                    sortState = null,
                    onSortStateChange = null,
                    navigateUp = navController::navigateUp,
                    onPlay = { index, shuffle ->
                        mainVM.onPlaybackEvent(PlaybackEvent.Play(history, index))
                    },
                    animatedContentScope = this,
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(history[it].id))
                    },
                )
            }
            composable<NavRoutes.LastAddedScreen> {
                ListScreen(
                    files = lastAdded,
                    title = stringResource(R.string.last_added),
                    sortState = null,
                    onSortStateChange = null,
                    navigateUp = navController::navigateUp,
                    onPlay = { index, shuffle ->
                        mainVM.onPlaybackEvent(PlaybackEvent.Play(lastAdded, index))
                             },
                    animatedContentScope = this,
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(lastAdded[it].id))
                    },
                )
            }
        }
    }
}
