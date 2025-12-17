package younesbouhouche.musicplayer.features.main.presentation.routes.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ExpressiveButton
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import com.younesb.mydesignsystem.presentation.components.IconContainer
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.presentation.components.SongListItem
import younesbouhouche.musicplayer.features.main.presentation.components.PictureCard
import younesbouhouche.musicplayer.features.main.presentation.util.expressiveRectShape
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    onArtistClick: (Artist) -> Unit,
    navigateToLibrary: () -> Unit,
) {
    val homeViewModel = koinViewModel<HomeViewModel>()
    val artists by homeViewModel.artists.collectAsState()
    val history by homeViewModel.history.collectAsState()
    val state = rememberCarouselState {
        artists.size
    }
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp).then(modifier),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = bottomPadding)
    ) {
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 36.dp, horizontal = 12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.welcome_back),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                    Text(
                        text = stringResource(R.string.welcome_text),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
        if (artists.isNotEmpty()) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Header(
                        Icons.Default.Person,
                        stringResource(R.string.top_artists),
                        stringResource(R.string.top_artists_text),
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
                                shape = rememberMaskShape(MaterialTheme.shapes.extraLarge),
                                contentPadding = PaddingValues(24.dp)
                            ) {
                                Column(
                                    Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                                ) {
                                    Text(
                                        artist.name,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Column(
                    Modifier.padding(vertical = 60.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val angle by rememberInfiniteTransition().animateFloat(0f, 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(20000, easing = { it }),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                    IconContainer(
                        Icons.Default.History,
                        Modifier.size(160.dp),
                        iconRatio = .4f,
                        shape = MaterialShapes.Cookie12Sided.toShape(angle.roundToInt())
                    )
                    Text(
                        stringResource(R.string.your_listening_history_will_appear_here),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExpressiveButton(
                        stringResource(R.string.explore_library),
                        ButtonDefaults.MediumContainerHeight,
                        outlined = true,
                        onClick = navigateToLibrary
                    )
                }
            }
        }
//        item {
//            ListContainer(
//                Icons.Default.Timer,
//                stringResource(R.string.recently_added),
//                stringResource(R.string.listen_to_your_recently_added_songs),
//                lastAdded,
//                onPlay = onPlay,
//            ) { }
//        }
//        item {
//            ListContainer(
//                Icons.Default.Favorite,
//                stringResource(R.string.favorites),
//                stringResource(R.string.favorites_subtitles),
//                favorites,
//                onPlay = onPlay,
//            ) { }
//        }
        item {
            ListContainer(
                Icons.Default.History,
                stringResource(R.string.history),
                stringResource(R.string.watch_your_recently_played_songs),
                history,
                onPlay = { songs, index ->
                    homeViewModel.play(songs.map { it.id }, index)
                }
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
    items: List<Song>,
    modifier: Modifier = Modifier,
    onPlay: (List<Song>, Int) -> Unit,
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
                onClick = onClick
            ) {
                ExpressiveIconButton(
                    Icons.Default.PlayArrow,
                    size = IconButtonDefaults.mediumIconSize,
                    widthOption = IconButtonDefaults.IconButtonWidthOption.Wide,
                    colors = IconButtonDefaults.filledTonalIconButtonColors()
                ) {
                    onPlay(items, 0)
                }
            }
            itemsSliced.forEachIndexed { index, it ->
                SongListItem(
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
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clip(expressiveRectShape(0, 2))
            .background(MaterialTheme.colorScheme.tertiary)
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
            tint = MaterialTheme.colorScheme.onTertiary
        )
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary,
                fontWeight = FontWeight.Medium
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)
            )
        }
        trailingContent?.invoke()
    }
}
