package younesbouhouche.musicplayer.main.presentation.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZ
import younesbouhouche.musicplayer.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.main.domain.models.Routes

@Composable
fun BoxScope.NavBar(
    route: Routes?,
    playing: Boolean,
    modifier: Modifier = Modifier,
    navigate: (NavRoutes) -> Unit
) {
    val spacing = 16.dp
    val density = LocalDensity.current
    var safeRoute by remember {
        mutableStateOf(
            route ?: Routes.Home
        )
    }
    LaunchedEffect(route) {
        if (route != null) safeRoute = route
    }
    var width by remember { mutableStateOf(0.dp) }
    var height by remember { mutableStateOf(100.dp) }
    val topCorner by animateDpAsState(if (playing) 8.dp else height / 2)
    val innerWidth = width - spacing * 2
    val offset by animateDpAsState(
        with(density) {
            (safeRoute.ordinal / Routes.entries.size.toFloat() * (innerWidth).toPx()).toDp() + spacing
        }
    )
    val indicatorColor by animateColorAsState(
        if (route == null) Color.Transparent
        else MaterialTheme.colorScheme.primary
    )
    Box(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .align(Alignment.BottomCenter)
    ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    width = with(density) { it.size.width.toDp() }
                    height = with(density) { it.size.height.toDp() }
                }
                .onSizeChanged {
                    width = with(density) { it.width.toDp() }
                    height = with(density) { it.height.toDp() }
                },
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(
                topCorner,
                topCorner,
                height / 2,
                height / 2,
            ),
            shadowElevation = 8.dp
        ) {
            Box(
                Modifier.fillMaxWidth().padding(vertical = spacing),
                contentAlignment = Alignment.CenterStart
            ) {
                // Add animated indicator
                Surface(
                    Modifier.height(40.dp)
                        .width(innerWidth / Routes.entries.size.toFloat())
                        .offset(offset)
                    ,
                    color = indicatorColor,
                    shape = RoundedCornerShape(100),
                ) {

                }
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = spacing),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Routes.entries.forEach { screen ->
                        val selected = route == screen
                        val color by animateColorAsState(
                            if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(
                            Modifier
                                .clip(RoundedCornerShape(100))
                                .height(40.dp)
                                .weight(1f)
                                .clickable {
                                    navigate(screen.destination)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            AnimatedContent(
                                selected,
                                transitionSpec = {
                                    materialSharedAxisZ(targetState)
                                }
                            ) {
                                Icon(
                                    if (it) screen.selectedIcon
                                    else screen.unselectedIcon,
                                    null,
                                    tint = color
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun NavBarPreview() {
    var route by remember {
        mutableStateOf<Routes?>(Routes.Home)
    }
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            NavBar(
                route = route,
                playing = false,
            ) { navRoute ->
                route =
                    Routes.entries.first { r -> r.destination == navRoute }
                        .takeIf { it != route }
            }
        }
    }
}