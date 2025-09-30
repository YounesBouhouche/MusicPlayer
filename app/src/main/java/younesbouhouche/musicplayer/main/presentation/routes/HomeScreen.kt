package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.presentation.util.IconContainer
import younesbouhouche.musicplayer.main.presentation.components.MusicCardListItem
import younesbouhouche.musicplayer.main.presentation.components.PictureCard
import younesbouhouche.musicplayer.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.settings.domain.models.ColorScheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    artists: List<Artist>,
    lastAdded: List<MusicCard>,
    favorites: List<MusicCard>,
    history: List<MusicCard>,
    modifier: Modifier = Modifier,
    onArtistClick: (Artist) -> Unit,
    onPlay: (List<MusicCard>, Int) -> Unit
) {
    val state = rememberCarouselState {
        artists.size
    }
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(0.dp, 16.dp, 0.dp, 260.dp)
    ) {
        item {
            Box(
                Modifier
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .clip(expressiveRectShape(0, 1))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
                    .fillMaxWidth()
                    .padding(28.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.welcome_back),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = stringResource(R.string.welcome_text),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
        if (artists.isNotEmpty()) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Header(
                        Icons.Default.Person,
                        stringResource(R.string.top_artists),
                        stringResource(R.string.top_artists_text),
                        colorScheme = ColorScheme.GREEN,
                    )
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = expressiveRectShape(1, 2)
                    ) {
                        HorizontalCenteredHeroCarousel(
                            state = state,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            itemSpacing = 16.dp,
                        ) {
                            val artist = artists[it]
                            PictureCard(
                                artist.getPicture(),
                                Icons.Default.Person,
                                {
                                    onArtistClick(artist)
                                },
                                Modifier.fillMaxWidth(),
                                shape = rememberMaskShape(MaterialTheme.shapes.extraLarge)
                            ) {
                                Column(
                                    Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                                ) {
                                    Text(
                                        artist.name,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            ListContainer(
                Icons.Default.Timer,
                stringResource(R.string.recently_added),
                stringResource(R.string.listen_to_your_recently_added_songs),
                lastAdded,
                colorScheme = ColorScheme.BLUE,
                onPlay = onPlay,
            ) { }
        }
        item {
            ListContainer(
                Icons.Default.Favorite,
                stringResource(R.string.favorites),
                stringResource(R.string.favorites_subtitles),
                favorites,
                colorScheme = ColorScheme.RED,
                onPlay = onPlay,
            ) { }
        }
        item {
            ListContainer(
                Icons.Default.History,
                stringResource(R.string.history),
                stringResource(R.string.watch_your_recently_played_songs),
                history,
                colorScheme = ColorScheme.PURPLE,
                onPlay = onPlay,
            ) { }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ListContainer(
    icon: ImageVector,
    title: String,
    subtitle: String,
    items: List<MusicCard>,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme? = null,
    onPlay: (List<MusicCard>, Int) -> Unit,
    onClick: () -> Unit,
) {
    val itemsSliced = items.take(2)
    AnimatedVisibility(
        items.isNotEmpty(),
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Header(
                icon,
                title,
                subtitle,
                colorScheme = colorScheme,
                onClick = onClick
            ) {
                IconContainer(
                    Icons.Default.PlayArrow,
                    Modifier.size(40.dp),
                    onClick = {
                        onPlay(items, 0)
                    },
                    shape = MaterialShapes.Cookie9Sided.toShape(),
                    iconTint = it.onPrimaryContainer,
                    background = it.onPrimary
                )
            }
            itemsSliced.forEachIndexed { index, it ->
                MusicCardListItem(
                    it,
                    modifier = Modifier.fillMaxWidth(),
                    shape = expressiveRectShape(index + 1, itemsSliced.size + 1),
                    onClick = {
                        onPlay(items, index)
                    }
                )
            }
        }
    }
}

@Composable
private fun Header(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable ((androidx.compose.material3.ColorScheme) -> Unit)? = null
) {
    val datastore = SettingsDataStore(LocalContext.current)
    val isDark by datastore.isDark().collectAsState(isSystemInDarkTheme())
    val colors = when(colorScheme) {
        null -> MaterialTheme.colorScheme
        else -> if(isDark) colorScheme.darkScheme else colorScheme.lightScheme
    }
    val contentColor = colors.onPrimary
    Row(
        modifier = modifier
            .clip(expressiveRectShape(0, 2))
            .background(Brush.linearGradient(listOf(colors.primary, colors.tertiary)))
            .clickable(onClick = { onClick?.invoke() }, enabled = onClick != null)
            .padding(18.dp, 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            null,
            Modifier.size(36.dp),
            tint = contentColor
        )
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
        trailingContent?.invoke(colors)
    }
}
