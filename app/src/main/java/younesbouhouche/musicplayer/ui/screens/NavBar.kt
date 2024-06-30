package younesbouhouche.musicplayer.ui.screens

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import younesbouhouche.musicplayer.models.NavRoutes
import younesbouhouche.musicplayer.models.Routes

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    state: Int,
    navigate: (NavRoutes) -> Unit
) {
    NavigationBar(modifier = modifier) {
        Routes.entries.forEachIndexed { index, screen ->
            NavigationBarItem(
                selected = index == state,
                alwaysShowLabel = false,
                icon = {
                    Icon(screen.icon, screen.title)
                },
                label = {
                    Text(screen.title)
                },
                onClick = {
                    navigate(screen.destination)
                }
            )
        }
    }
}