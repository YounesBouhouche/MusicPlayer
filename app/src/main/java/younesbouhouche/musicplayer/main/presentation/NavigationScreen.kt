package younesbouhouche.musicplayer.main.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
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

@Composable
fun NavigationScreen(
    navController: NavHostController,
    mainVM: MainVM,
    modifier: Modifier = Modifier,
) {
    val filesSorted by mainVM.filesSorted.collectAsState()
    val recentlyAdded by mainVM.recentlyAdded.collectAsState()
    val sortState by mainVM.sortState.collectAsState()
    val mostPlayedArtists by mainVM.mostPlayedArtists.collectAsState()

    val albumsSorted by mainVM.albumsSorted.collectAsState()
    val albumsSortState by mainVM.albumsSortState.collectAsState()

    val artistsSorted by mainVM.artistsSorted.collectAsState()
    val artistsSortState by mainVM.artistsSortState.collectAsState()

    val playlistsSorted by mainVM.playlistsSorted.collectAsState()
    val playlistsSortState by mainVM.playlistsSortState.collectAsState()
    val playlistSortState by mainVM.playlistSortState.collectAsState()
    val playlist by mainVM.playlist.collectAsState()
    val playlistFiles by mainVM.playlistFiles.collectAsState()

    val timestamps by mainVM.timestampsCards.collectAsState()
    val recentlyPlayedFiles by mainVM.recentlyPlayed.collectAsState()

    val mostPlayedFiles = timestamps.toList().sortedByDescending { it.second.size }.map { it.first }

    val listScreenFiles by mainVM.listScreenFiles.collectAsState()
    val listScreenSortState by mainVM.listScreenSortState.collectAsState()

    val favoritesFiles by mainVM.favoritesFiles.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home,
        enterTransition = { materialFadeThroughIn() },
        exitTransition = { materialFadeThroughOut() },
        modifier = modifier.fillMaxSize(),
    ) {
        composable<NavRoutes.Home> {
            Home(
                navController::navigate,
                {
                    mainVM.setListFiles(it.items)
                    navController.navigate(NavRoutes.ListScreen(it.name))
                },
                {
                    mainVM.onUiEvent(
                        UiEvent.ShowListBottomSheet(
                            it.items,
                            it.name,
                            "${it.items.size} item(s)",
                            it.cover,
                            Icons.Default.AccountCircle,
                        ),
                    )
                },
                mostPlayedArtists,
            )
        }
        composable<NavRoutes.Albums> {
            Albums(
                albumsSorted,
                {
                    mainVM.setListFiles(it.items)
                    navController.navigate(NavRoutes.ListScreen(it.title))
                },
                {
                    mainVM.onUiEvent(
                        UiEvent.ShowListBottomSheet(
                            it.items,
                            it.title,
                            "${it.items.size} item(s)",
                            it.cover,
                            Icons.Default.Album,
                        ),
                    )
                },
                Modifier,
                albumsSortState,
                mainVM::onPlayerEvent,
                mainVM::onAlbumsSortEvent,
            )
        }
        composable<NavRoutes.Artists> {
            Artists(
                artistsSorted,
                {
                    mainVM.setListFiles(it.items)
                    navController.navigate(NavRoutes.ListScreen(it.name))
                },
                {
                    mainVM.onUiEvent(
                        UiEvent.ShowListBottomSheet(
                            it.items,
                            it.name,
                            "${it.items.size} item(s)",
                            it.cover,
                            Icons.Default.AccountCircle,
                        ),
                    )
                },
                Modifier,
                artistsSortState,
                mainVM::onPlayerEvent,
                mainVM::onArtistsSortEvent,
            )
        }
        composable<NavRoutes.Playlists> {
            Playlists(
                playlistsSorted,
                {
                    mainVM.setCurrentPlaylist(it)
                    with(playlistsSorted[it]) {
                        navController.navigate(NavRoutes.PlaylistScreen(name))
                    }
                },
                { mainVM.onUiEvent(UiEvent.ShowPlaylistBottomSheet(it)) },
                Modifier,
                playlistsSortState,
                mainVM::onPlayerEvent,
                mainVM::onUiEvent,
                { mainVM.onPlaylistsSortEvent(it) },
            )
        }
        composable<NavRoutes.Library> {
            Library(
                filesSorted,
                Modifier,
                sortState,
                mainVM::onSortEvent,
                mainVM::onUiEvent,
            ) {
                mainVM.onPlayerEvent(PlayerEvent.Play(filesSorted, it))
            }
        }
        composable<NavRoutes.ListScreen> { entry ->
            val route = entry.toRoute<NavRoutes.ListScreen>()
            ListScreen(
                listScreenFiles,
                route.title,
                listScreenSortState,
                mainVM::onListSortEvent,
                navController::navigateUp,
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(listScreenFiles[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(listScreenFiles, index))
            }
        }
        composable<NavRoutes.PlaylistScreen> { entry ->
            val route = entry.toRoute<NavRoutes.PlaylistScreen>()
            PlaylistScreen(
                playlistFiles,
                route.title,
                playlistSortState,
                mainVM::onPlaylistSortEvent,
                navController::navigateUp,
                { from, to ->
                    mainVM.onPlaylistEvent(PlaylistEvent.Reorder(playlist, from, to))
                },
                {
                    mainVM.onPlaylistEvent(PlaylistEvent.RemoveAt(playlist, it))
                },
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(playlistFiles[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(playlistFiles, index))
            }
        }
        composable<NavRoutes.FavoritesScreen> {
            ListScreen(
                favoritesFiles,
                "Favorites",
                listScreenSortState,
                mainVM::onListSortEvent,
                navController::navigateUp,
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(favoritesFiles[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(favoritesFiles, index))
            }
        }
        composable<NavRoutes.MostPlayedScreen> {
            ListScreen(
                mostPlayedFiles,
                "Most Played",
                null,
                null,
                navController::navigateUp,
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(mostPlayedFiles[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(mostPlayedFiles, index))
            }
        }
        composable<NavRoutes.RecentlyPlayedScreen> {
            ListScreen(
                recentlyPlayedFiles,
                "Recently Played",
                null,
                null,
                navController::navigateUp,
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(recentlyPlayedFiles[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(recentlyPlayedFiles, index))
            }
        }
        composable<NavRoutes.RecentlyAddedScreen> {
            ListScreen(
                recentlyAdded,
                "Recently Added",
                null,
                null,
                navController::navigateUp,
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(recentlyAdded[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(recentlyAdded, index))
            }
        }
    }
}