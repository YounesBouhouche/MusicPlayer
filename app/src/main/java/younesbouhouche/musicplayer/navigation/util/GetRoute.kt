package younesbouhouche.musicplayer.navigation.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun <T> NavHostController.getRoute(
    routesList: List<T>,
    routeMapper: (T) -> String = { it?.javaClass?.kotlin?.qualifiedName ?: "" }
): T? = currentBackStackEntryAsState().value?.getRoute(routesList, routeMapper)

fun <T> NavBackStackEntry.getRoute(
    routesList: List<T>,
    routeMapper: (T) -> String = { it?.javaClass?.kotlin?.qualifiedName ?: "" }
): T?  {
    val currentRoute = destination.route ?: return null
    return routesList.find { routeMapper(it) == currentRoute }
}
