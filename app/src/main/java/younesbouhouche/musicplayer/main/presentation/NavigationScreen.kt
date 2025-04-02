package younesbouhouche.musicplayer.main.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.main.presentation.routes.Albums
import younesbouhouche.musicplayer.main.presentation.routes.Artists
import younesbouhouche.musicplayer.main.presentation.routes.Home
import younesbouhouche.musicplayer.main.presentation.routes.Library
import younesbouhouche.musicplayer.main.presentation.routes.ListScreen
import younesbouhouche.musicplayer.main.presentation.routes.PlaylistScreen
import younesbouhouche.musicplayer.main.presentation.routes.Playlists
import younesbouhouche.musicplayer.main.presentation.viewmodel.MainVM

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavigationScreen(
    navController: NavHostController,
    mainVM: MainVM,
    modifier: Modifier = Modifier,
) {
    val filesSorted by mainVM.filesSorted.collectAsState()
    val lastAdded by mainVM.lastAdded.collectAsState()
    val recentlyAdded = if (lastAdded.size > 5) lastAdded.subList(0, 5) else lastAdded
    val sortState by mainVM.sortState.collectAsState()
    val mostPlayedArtists by mainVM.mostPlayedArtists.collectAsState()

    val albumsSorted by mainVM.albumsSorted.collectAsState()
    val albumsSortState by mainVM.albumsSortState.collectAsState()

    val artistsSorted by mainVM.artistsSorted.collectAsState()
    val artistsSortState by mainVM.artistsSortState.collectAsState()

    val playlistsSorted by mainVM.playlistsSorted.collectAsState()
    val playlistsSortState by mainVM.playlistsSortState.collectAsState()
    val playlistSortState by mainVM.playlistSortState.collectAsState()
    val playlist by mainVM.uiPlaylist.collectAsState()

    val history by mainVM.history.collectAsState()
    val mostPlayed by mainVM.mostPlayed.collectAsState()
    val mostFivePlayed = if (mostPlayed.size > 5) mostPlayed.subList(0, 5) else mostPlayed

    val listScreenSortState by mainVM.listScreenSortState.collectAsState()

    val favoritesFiles by mainVM.favoritesFiles.collectAsState()

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
                    play = { list, index -> mainVM.onPlayerEvent(PlayerEvent.Play(list, index)) },
                    showInfo = { mainVM.onUiEvent(UiEvent.ShowBottomSheet(it)) },
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
                    albums = albumsSorted,
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
                        mainVM.onPlayerEvent(PlayerEvent.Play(album.items, index, shuffle))
                    },
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(album.items[it]))
                    },
                    animatedContentScope = this,
                    key = "album-${album.name}",
                    cover = album.cover,
                    icon = Icons.Default.Album
                )
            }
            composable<NavRoutes.Artists> {
                Artists(
                    artists = artistsSorted,
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
                        mainVM.onPlayerEvent(PlayerEvent.Play(artist.items, index, shuffle))
                    },
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(artist.items[it]))
                    },
                    animatedContentScope = this,
                    key = "artist-${artist.name}",
                    cover = artist.getPicture(),
                    icon = Icons.Default.Person
                )
            }
            composable<NavRoutes.Playlists> {
                Playlists(
                    playlists = playlistsSorted,
                    onClick = {
                        mainVM.setCurrentPlaylist(it)
                        with(playlistsSorted[it]) {
                            navController.navigate(NavRoutes.PlaylistScreen(name))
                        }
                    },
                    onLongClick = { mainVM.onUiEvent(UiEvent.ShowPlaylistBottomSheet(it)) },
                    modifier = Modifier,
                    sortState = playlistsSortState,
                    onSortStateChange = mainVM::onPlaylistsSortChange,
                    onPlayerEvent = mainVM::onPlayerEvent,
                    onPlaylistEvent = mainVM::onPlaylistEvent,
                    onUiEvent = mainVM::onUiEvent,
                    animatedContentScope = this
                )
            }
            composable<NavRoutes.Library> {
                Library(
                    filesSorted,
                    Modifier.testTag("library_list"),
                    sortState,
                    mainVM::onLibrarySortChange,
                    mainVM::onUiEvent,
                ) {
                    mainVM.onPlayerEvent(PlayerEvent.Play(filesSorted, it))
                }
            }
            composable<NavRoutes.PlaylistScreen> { entry ->
                PlaylistScreen(
                    playlist = playlist,
                    sortState = playlistSortState,
                    onSortStateChange = mainVM::onPlaylistScreenSortChange,
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
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(playlist.items[it]))
                    },
                    onPlay = { index, shuffle ->
                        mainVM.onPlayerEvent(PlayerEvent.Play(playlist.items, index, shuffle))
                    }
                )
            }
            composable<NavRoutes.FavoritesScreen> {
                ListScreen(
                    files = favoritesFiles,
                    title = stringResource(R.string.favorites),
                    sortState = listScreenSortState,
                    onSortStateChange = mainVM::onListScreenSortChange,
                    navigateUp = navController::navigateUp,
                    onPlay = { index, shuffle ->
                        mainVM.onPlayerEvent(PlayerEvent.Play(favoritesFiles, index, shuffle))
                    },
                    animatedContentScope = this,
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(favoritesFiles[it]))
                    },
                )
            }
            composable<NavRoutes.MostPlayedScreen> {
                ListScreen(
                    files = mostFivePlayed,
                    title = stringResource(R.string.most_played),
                    sortState = null,
                    onSortStateChange = null,
                    navigateUp = navController::navigateUp,
                    onPlay = { index, shuffle ->
                        mainVM.onPlayerEvent(PlayerEvent.Play(mostFivePlayed, index, shuffle))
                    },
                    animatedContentScope = this,
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(mostFivePlayed[it]))
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
                        mainVM.onPlayerEvent(PlayerEvent.Play(history, index, shuffle))
                    },
                    animatedContentScope = this,
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(history[it]))
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
                        mainVM.onPlayerEvent(PlayerEvent.Play(lastAdded, index, shuffle))
                             },
                    animatedContentScope = this,
                    onLongClick = {
                        mainVM.onUiEvent(UiEvent.ShowBottomSheet(lastAdded[it]))
                    },
                )
            }
        }
    }
}
