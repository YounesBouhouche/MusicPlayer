package younesbouhouche.musicplayer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.models.NavRoutes
import younesbouhouche.musicplayer.models.Routes
import younesbouhouche.musicplayer.ui.isCompact
import younesbouhouche.musicplayer.ui.navBarHeight
import kotlin.math.roundToInt

@Composable
fun BoxScope.NavBar(
    visible: Boolean,
    progress: Float,
    state: Int,
    navigate: (NavRoutes) -> Unit
) {
    val navBarHeight = navBarHeight
    if (isCompact)
        AnimatedVisibility(
            modifier = Modifier
            .align(Alignment.BottomStart)
            .offset {
                IntOffset(
                    0,
                    ((80 + navBarHeight.roundToPx()) * progress).roundToInt().dp.roundToPx()
                )
            },
            visible = visible,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            NavigationBar {
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
    else
        NavigationRail(
            modifier = Modifier.align(Alignment.TopStart).displayCutoutPadding(),
            windowInsets = WindowInsets.systemBars
        ) {
            Routes.entries.forEachIndexed { index, screen ->
                NavigationRailItem(
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