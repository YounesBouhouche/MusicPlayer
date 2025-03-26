package younesbouhouche.musicplayer.main.presentation

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.main.presentation.util.composables.isCompact
import younesbouhouche.musicplayer.main.presentation.util.composables.leftEdgeWidth
import younesbouhouche.musicplayer.main.presentation.util.composables.navBarHeight
import younesbouhouche.musicplayer.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.main.domain.models.Routes
import kotlin.math.roundToInt

@Composable
fun BoxScope.NavBar(
    visible: Boolean,
    progress: Float,
    route: Routes,
    navigate: (NavRoutes) -> Unit,
) {
    val navBarHeight = navBarHeight
    val leftEdgeWidth = leftEdgeWidth
    if (isCompact) {
        AnimatedVisibility(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .offset {
                        IntOffset(
                            0,
                            ((80 + navBarHeight.roundToPx()) * progress).roundToInt().dp.roundToPx(),
                        )
                    },
            visible = visible,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
        ) {
            NavigationBar {
                Routes.entries.forEach { screen ->
                    NavigationBarItem(
                        modifier = Modifier.testTag("nav_${screen.name.lowercase()}"),
                        selected = route == screen,
                        alwaysShowLabel = false,
                        icon = {
                            Icon(screen.icon, null)
                        },
                        label = {
                            Text(
                                stringResource(screen.title),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        onClick = {
                            navigate(screen.destination)
                        },
                    )
                }
            }
        }
    } else {
        NavigationRail(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .displayCutoutPadding()
                    .offset {
                        IntOffset(
                            (-(80 + leftEdgeWidth.roundToPx()) * progress).roundToInt().dp.roundToPx(),
                            0,
                        )
                    },
            windowInsets = WindowInsets.systemBars,
        ) {
            Routes.entries.forEach { screen ->
                NavigationRailItem(
                    selected = screen == route,
                    alwaysShowLabel = false,
                    icon = {
                        Icon(screen.icon, null)
                    },
                    label = {
                        Text(stringResource(screen.title))
                    },
                    onClick = {
                        navigate(screen.destination)
                    },
                )
            }
        }
    }
}
