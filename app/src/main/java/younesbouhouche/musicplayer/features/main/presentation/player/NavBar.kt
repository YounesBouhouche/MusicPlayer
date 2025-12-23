package younesbouhouche.musicplayer.features.main.presentation.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import soup.compose.material.motion.animation.materialSharedAxisZ
import younesbouhouche.musicplayer.features.main.presentation.navigation.MainNavRoute
import younesbouhouche.musicplayer.features.main.presentation.navigation.TopLevelRoutes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BoxScope.NavBar(
    route: TopLevelRoutes?,
    playing: Boolean,
    modifier: Modifier = Modifier,
    navigate: (MainNavRoute) -> Unit
) {
    val spacing = 8.dp
    val density = LocalDensity.current
    val buttonHeight = 48.dp
    var safeRoute by remember {
        mutableStateOf(
            route ?: TopLevelRoutes.Home
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
            (safeRoute.ordinal / TopLevelRoutes.entries.size.toFloat() * (innerWidth).toPx()).toDp() + spacing
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
    )
    val indicatorColor by animateColorAsState(
        if (route == null) Color.Transparent
        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = .5f),
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
    )
    Box(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
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
                    Modifier.height(buttonHeight)
                        .width(innerWidth / TopLevelRoutes.entries.size.toFloat())
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
                    TopLevelRoutes.entries.forEach { screen ->
                        val selected = route == screen
                        val color by animateColorAsState(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
                        )
                        val offset by animateDpAsState(
                            if (selected) 5.dp else 0.dp,
                            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
                        )
                        Column(
                            Modifier
                                .clip(RoundedCornerShape(100))
                                .height(buttonHeight)
                                .weight(1f)
                                .clickable {
                                    navigate(screen.destination)
                                },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AnimatedContent(
                                selected,
                                transitionSpec = {
                                    materialSharedAxisZ(targetState)
                                },
                                modifier = Modifier.weight(1f, false),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (it) screen.selectedIcon
                                    else screen.unselectedIcon,
                                    null,
                                    Modifier.size(24.dp).offset(y = offset),
                                    tint = color
                                )
                            }
                            AnimatedVisibility(
                                selected,
                                enter = expandVertically(expandFrom = Alignment.Top),
                                exit = shrinkVertically(shrinkTowards = Alignment.Top),
                                modifier = Modifier.weight(1f, false)
                            ) {
                                Column {
                                    Text(
                                        stringResource(screen.title),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 8.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
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
        mutableStateOf<TopLevelRoutes?>(TopLevelRoutes.Home)
    }
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            NavBar(
                route = route,
                playing = false,
            ) { navRoute ->
                route =
                    TopLevelRoutes.entries.first { r -> r.destination == navRoute }
                        .takeIf { it != route }
            }
        }
    }
}