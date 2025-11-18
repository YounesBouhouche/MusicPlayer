package younesbouhouche.musicplayer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import younesbouhouche.musicplayer.features.main.presentation.layout.MainNavGraph
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.permissions.presentation.PermissionsNavGraph
import younesbouhouche.musicplayer.features.settings.presentation.SettingsNavGraph
import younesbouhouche.musicplayer.navigation.routes.Graph
import younesbouhouche.musicplayer.navigation.util.getRoute

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    mainVM: MainViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val width = LocalView.current.width
    NavHost(
        navController,
        startDestination = Graph.Permissions,
        modifier = modifier,
        enterTransition = {
            materialSharedAxisXIn(
                (initialState.getRoute(Graph.graphs)?.ordinal
                    ?: 0) < (targetState.getRoute(Graph.graphs)?.ordinal ?: 1),
                width / 2
            )
        },
        exitTransition = {
            materialSharedAxisXOut(
                (initialState.getRoute(Graph.graphs)?.ordinal
                    ?: 0) < (targetState.getRoute(Graph.graphs)?.ordinal ?: 1),
                width / 2
            )
        }
    ) {
        composable<Graph.Permissions> {
            PermissionsNavGraph()
        }
        composable<Graph.Main> {
            MainNavGraph(mainVM) {
                navController.navigate(Graph.Settings)
            }
        }
        composable<Graph.Settings> {
            SettingsNavGraph()
        }
    }
}