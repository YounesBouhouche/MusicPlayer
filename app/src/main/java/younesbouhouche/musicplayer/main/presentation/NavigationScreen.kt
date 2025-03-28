package younesbouhouche.musicplayer.main.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Album
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
    val _mostPlayed by mainVM.mostPlayed.collectAsState()
    val mostPlayed = if (_mostPlayed.size > 5) _mostPlayed.subList(0, 5) else _mostPlayed

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
                recentlyAdded,
                mostPlayed,
                { list, index -> mainVM.onPlayerEvent(PlayerEvent.Play(list, index)) },
                { mainVM.onUiEvent(UiEvent.ShowBottomSheet(it)) },
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
                mainVM::onAlbumsSortChange,
                mainVM::onPlayerEvent,
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
                mainVM::onArtistsSortChange,
                mainVM::onPlayerEvent,
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
                mainVM::onPlaylistsSortChange,
                mainVM::onPlayerEvent,
                mainVM::onPlaylistEvent,
                mainVM::onUiEvent,
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
        composable<NavRoutes.ListScreen> { entry ->
            val route = entry.toRoute<NavRoutes.ListScreen>()
            ListScreen(
                listScreenFiles,
                route.title,
                listScreenSortState,
                mainVM::onListScreenSortChange,
                navController::navigateUp,
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(listScreenFiles[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(listScreenFiles, index))
            }
        }
        composable<NavRoutes.PlaylistScreen> { entry ->
            PlaylistScreen(
                playlist,
                playlistSortState,
                mainVM::onPlaylistScreenSortChange,
                navController::navigateUp,
                { from, to ->
                    mainVM.onPlaylistEvent(PlaylistEvent.Reorder(playlist, from, to))
                },
                {
                    mainVM.onPlaylistEvent(PlaylistEvent.RemoveAt(playlist, it))
                },
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(playlist.items[it]))
                },
                {
                    mainVM.onPlayerEvent(PlayerEvent.Play(playlist.items))
                },
                {
                    mainVM.onPlayerEvent(PlayerEvent.Play(playlist.items, shuffle = true))
                }
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(playlist.items, index))
            }
        }
        composable<NavRoutes.FavoritesScreen> {
            ListScreen(
                favoritesFiles,
                stringResource(R.string.favorites),
                listScreenSortState,
                mainVM::onListScreenSortChange,
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
                mostPlayed,
                stringResource(R.string.most_played),
                null,
                null,
                navController::navigateUp,
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(mostPlayed[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(mostPlayed, index))
            }
        }
        composable<NavRoutes.HistoryScreen> {
            ListScreen(
                history,
                stringResource(R.string.history),
                null,
                null,
                navController::navigateUp,
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(history[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(history, index))
            }
        }
        composable<NavRoutes.LastAddedScreen> {
            ListScreen(
                lastAdded,
                stringResource(R.string.last_added),
                null,
                null,
                navController::navigateUp,
                {
                    mainVM.onUiEvent(UiEvent.ShowBottomSheet(lastAdded[it]))
                },
            ) { index ->
                mainVM.onPlayerEvent(PlayerEvent.Play(lastAdded, index))
            }
        }
    }
}
