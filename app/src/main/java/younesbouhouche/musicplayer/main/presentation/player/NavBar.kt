package younesbouhouche.musicplayer.main.presentation.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
    val topCorner by animateDpAsState(if (playing) 8.dp else 60.dp)
    val bottomCorner by animateDpAsState(if (playing) 40.dp else 60.dp)
    Box(
        modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .align(Alignment.BottomCenter)
    ) {
        Surface(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(
                topCorner,
                topCorner,
                bottomCorner,
                bottomCorner
            ),
            shadowElevation = 8.dp
        ) {
            NavigationBar(
                Modifier.padding(top = 4.dp, bottom = 2.dp).fillMaxWidth(),
                containerColor = Color.Transparent,
                windowInsets = WindowInsets()
            ) {
                Routes.entries.forEach { screen ->
                    val selected = route == screen
                    val weight by animateFloatAsState(if (selected) 1.4f else 1f)
                    NavigationBarItem(
                        selected = selected,
                        alwaysShowLabel = false,
                        modifier = Modifier.weight(weight),
                        icon = {
                            AnimatedContent(
                                selected,
                                transitionSpec = { materialSharedAxisZ(true) }
                            ) {
                                Icon(
                                    if (it) screen.selectedIcon
                                    else screen.unselectedIcon,
                                    null,
                                )
                            }
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
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(.2f),
                        )
                    )
                }
            }
        }
    }
}