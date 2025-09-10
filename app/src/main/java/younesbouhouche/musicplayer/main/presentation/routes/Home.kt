package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shuffle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.main.presentation.components.HomeMusicCard
import younesbouhouche.musicplayer.main.presentation.components.LazyVerticalGridWithHeader
import younesbouhouche.musicplayer.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Home(
    navigate: (NavRoutes) -> Unit,
    recentlyAdded: List<MusicCard>,
    mostPlayed: List<MusicCard>,
    play: (List<MusicCard>, Int) -> Unit,
    showInfo: (MusicCard) -> Unit,
    navigateToArtist: (Artist) -> Unit,
    showArtistBottomSheet: (Artist) -> Unit,
    artists: List<Artist>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGridWithHeader(GridCells.Fixed(1), modifier) {
        item {
            Spacer(Modifier.height(16.dp))
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HeaderButton(
                    stringResource(R.string.favorites),
                    Icons.Default.Favorite,
                    tint = MaterialTheme.colorScheme.error,
                ) { navigate(NavRoutes.Favorites) }
                HeaderButton(
                    stringResource(R.string.most_played),
                    Icons.AutoMirrored.Filled.TrendingUp,
                    tint = MaterialTheme.colorScheme.secondary,
                ) { }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HeaderButton(
                    stringResource(R.string.history),
                    Icons.Default.History,
                    tint = MaterialTheme.colorScheme.primary,
                ) { navigate(NavRoutes.HistoryScreen) }
                HeaderButton(
                    stringResource(R.string.shuffle),
                    Icons.Default.Shuffle,
                    tint = MaterialTheme.colorScheme.tertiary,
                ) { }
            }
        }
        if (recentlyAdded.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
            }
            item {
                Row(Modifier.fillMaxWidth().padding(16.dp, 24.dp).animateItem()) {
                    Text(
                        "Recently Added",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            item {
                LazyRow(
                    Modifier.fillMaxWidth().animateItem(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    itemsIndexed(recentlyAdded, { index, item -> item.id }) { index, item ->
                        HomeMusicCard(
                            item,
                            Modifier.animateItem(),
                            onLongClick = { showInfo(item) }
                        ) {
                            play(recentlyAdded, index)
                        }
                    }
                }
            }
        }
        if (mostPlayed.isNotEmpty()) {
            item {
                Row(Modifier.fillMaxWidth().padding(16.dp, 24.dp).animateItem()) {
                    Text(
                        "Most Played",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            item {
                LazyRow(
                    Modifier.fillMaxWidth().animateItem(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    itemsIndexed(mostPlayed, { index, item -> item.id }) { index, item ->
                        HomeMusicCard(item, Modifier.animateItem(), onLongClick = { showInfo(item) }) {
                            play(mostPlayed, index)
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
        }
        if (!artists.isEmpty()) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 24.dp),
                ) {
                    Text(
                        stringResource(R.string.most_played_artists),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            item {
                val state =
                    rememberCarouselState {
                        minOf(5, artists.size)
                    }
                HorizontalMultiBrowseCarousel(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    itemSpacing = 8.dp,
                    contentPadding = PaddingValues(16.dp, 8.dp),
                    preferredItemWidth = 200.dp,
                    minSmallItemWidth = 200.dp,
                    maxSmallItemWidth = 250.dp
                ) { index ->
                    artists.getOrNull(index)?.let {
                        Column(
                            Modifier
                                .alpha(carouselItemDrawInfo.size / carouselItemDrawInfo.maxSize)
                                .fillMaxWidth()
                            ) {
                            SubcomposeAsyncImage(
                                model = it.getPicture(),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(rememberMaskShape(CircleShape))
                                    .size(200.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .combinedClickable(
                                        onLongClick = {
                                            showArtistBottomSheet(it)
                                        },
                                    ) { navigateToArtist(it) },
                                error = {
                                    Box(Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Person,
                                            null,
                                            Modifier.size(120.dp),
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            )
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

@Composable
private fun RowScope.HeaderButton(
    label: String,
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    AppTheme(primary = tint) {
        ExtendedFloatingActionButton(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            text = {
                Text(label)
            },
            icon = {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            },
            modifier = Modifier.weight(1f),
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            onClick = onClick,
        )
    }
}
