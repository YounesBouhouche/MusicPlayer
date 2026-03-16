package younesbouhouche.musicplayer.features.main.presentation.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
fun NavBar(
    route: TopLevelRoutes?,
    playing: Boolean,
    modifier: Modifier = Modifier,
    navigate: (MainNavRoute) -> Unit
) {
    val topRadius by animateDpAsState(
        if (playing) 8.dp else 40.dp
    )
    val shape = remember(topRadius) {
        RoundedCornerShape(
            topStart = topRadius,
            topEnd = topRadius,
            bottomEnd = 40.dp,
            bottomStart = 40.dp
        )
    }
    Box(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth()
            .height(80.dp)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            Modifier.fillMaxSize().padding(8.dp),
        ) {
            TopLevelRoutes.entries.forEach { navRoute ->
                val selected = route == navRoute
                val color by animateColorAsState(
                    if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                )
                val weight by animateFloatAsState(
                    if (selected) 2f else 1f
                )
                Row(
                    modifier = Modifier.height(64.dp)
                        .weight(weight)
                        .clip(RoundedCornerShape(100))
                        .background(color).clickable {
                            navigate(navRoute.destination)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AnimatedContent(
                        targetState = selected,
                        transitionSpec = {
                            materialSharedAxisZ(true).using(
                                SizeTransform(clip = false)
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if (it) navRoute.selectedIcon else navRoute.unselectedIcon,
                            contentDescription = stringResource(id = navRoute.title),
                            tint = if (it) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    AnimatedContent(
                        targetState = selected,
                        transitionSpec = {
                            materialSharedAxisZ(true).using(
                                SizeTransform(clip = false)
                            )
                        },
                        contentAlignment = Alignment.CenterStart
                    ) { targetSelected ->
                        if (targetSelected) {
                            Text(
                                text = stringResource(id = navRoute.title),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 8.dp)
                            )
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
                modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding(),
            ) { navRoute ->
                route =
                    TopLevelRoutes.entries.first { r -> r.destination == navRoute }
                        .takeIf { it != route }
            }
        }
    }
}