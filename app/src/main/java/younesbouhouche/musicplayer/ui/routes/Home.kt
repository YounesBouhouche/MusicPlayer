package younesbouhouche.musicplayer.ui.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.NavRoutes
import younesbouhouche.musicplayer.ui.components.LazyColumnWithHeader

@Composable
fun Home(
    navigate: (NavRoutes) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumnWithHeader(
        modifier = modifier,
        leadingContent = {}
    ) {
        item {
            Spacer(Modifier.height(16.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExtendedFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    text = {
                        Text("Last added")
                    },
                    icon = {
                        Icon(
                            Icons.Default.LibraryAdd,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    onClick = { navigate(NavRoutes.RecentlyAddedScreen) })
                ExtendedFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    text = {
                        Text("Most played")
                    },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Default.TrendingUp,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    onClick = { navigate(NavRoutes.MostPlayedScreen) })
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExtendedFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    text = {
                        Text("Favorites")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Favorite,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    onClick = { navigate(NavRoutes.FavoritesScreen) })
                ExtendedFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    text = {
                        Text("History")
                    },
                    icon = {
                        Icon(
                            Icons.Default.History,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    onClick = { /*TODO*/ })
            }
        }
    }
}