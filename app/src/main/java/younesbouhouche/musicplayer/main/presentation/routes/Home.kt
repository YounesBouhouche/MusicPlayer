package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.core.presentation.LazyColumnWithHeader
import younesbouhouche.musicplayer.main.domain.models.Artist
import younesbouhouche.musicplayer.main.domain.models.NavRoutes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Home(
    navigate: (NavRoutes) -> Unit,
    navigateToArtist: (Artist) -> Unit,
    showArtistBottomSheet: (Artist) -> Unit,
    artists: List<Artist>,
    modifier: Modifier = Modifier,
) {
    LazyColumnWithHeader(
        modifier = modifier,
        leadingContent = {},
    ) {
        item {
            Spacer(Modifier.height(16.dp))
        }
        item {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HeaderButton(
                    "Last added",
                    Icons.Default.LibraryAdd,
                ) { navigate(NavRoutes.LastAddedScreen) }
                HeaderButton(
                    "Most played",
                    Icons.AutoMirrored.Default.TrendingUp,
                ) { navigate(NavRoutes.MostPlayedScreen) }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
        }
        item {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HeaderButton(
                    "Favorites",
                    Icons.Default.Favorite,
                ) { navigate(NavRoutes.FavoritesScreen) }
                HeaderButton(
                    "History",
                    Icons.Default.History,
                ) { navigate(NavRoutes.HistoryScreen) }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 24.dp),
            ) {
                Text(
                    "Most played artists",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        item {
            AnimatedContent(targetState = artists, label = "") { artistsList ->
                if (artistsList.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 60.dp),
                    ) {
                        Icon(Icons.Default.AccountCircle, null, Modifier.size(120.dp))
                        Text(
                            text = "No enough data for most played artists",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    val state =
                        rememberCarouselState {
                            minOf(5, artists.size)
                        }
                    HorizontalMultiBrowseCarousel(
                        state = state,
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(16.dp),
                        preferredItemWidth = 200.dp,
                    ) {
                        artists.getOrNull(it)?.let {
                            Column(
                                Modifier
                                    .alpha(carouselItemInfo.size / carouselItemInfo.maxSize),
                            ) {
                                AnimatedContent(
                                    targetState = it.cover,
                                    label = "",
                                    modifier = Modifier.fillMaxSize(),
                                ) { bitmap ->
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .background(
                                                MaterialTheme.colorScheme.surfaceContainer,
                                                rememberMaskShape(CircleShape),
                                            )
                                            .clip(rememberMaskShape(CircleShape))
                                            .clipToBounds()
                                            .combinedClickable(
                                                onLongClick = {
                                                    showArtistBottomSheet(it)
                                                },
                                            ) { navigateToArtist(it) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (bitmap == null) {
                                            Icon(
                                                Icons.Default.Person,
                                                null,
                                                Modifier.size(120.dp),
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        } else {
                                            Image(
                                                bitmap = bitmap.asImageBitmap(),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop,
                                            )
                                        }
                                    }
                                }
                                Text(
                                    it.name,
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.HeaderButton(
    label: String,
    icon: ImageVector,
    navigate: () -> Unit,
) {
    ExtendedFloatingActionButton(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        text = {
            Text(label)
        },
        icon = {
            Icon(
                icon,
                null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .weight(1f),
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        onClick = navigate,
    )
}
