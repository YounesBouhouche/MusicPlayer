package younesbouhouche.musicplayer.features.main.presentation.routes.home

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.presentation.components.ScreenHeader
import younesbouhouche.musicplayer.features.main.presentation.navigation.MainNavRoute

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    albums: List<Album>,
    artists: List<Artist>,
    lastAdded: List<Song>,
    history: List<Song>,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    onArtistClick: (Artist) -> Unit,
    navigateTo: (MainNavRoute) -> Unit,
    play: (List<Long>, Int, Boolean) -> Unit
) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(bottom = bottomPadding, top = 30.dp)
    ) {
        item {
            ListContainer(
                title = "Discover Albums",
                subtitle = "Based on your listening history",
                actions = {
                    OutlinedButton(
                        onClick = {},
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(16.dp, 10.dp)
                    ) {
                        Text("View All")
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Icon(Icons.AutoMirrored.Default.ArrowForward, null)
                    }
                }
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val state = rememberCarouselState {
                        albums.size
                    }
                    HorizontalCenteredHeroCarousel(
                        state = state,
                        itemSpacing = 8.dp,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    ) { index ->
                        val album = albums[index]
                        val opacity by remember(state) {
                            derivedStateOf {
                                carouselItemDrawInfo.size / carouselItemDrawInfo.maxSize
                            }
                        }
                        AlbumCard(
                            album,
                            rememberMaskShape(MaterialTheme.shapes.extraLarge),
                            opacity = opacity
                        ) {
                            navigateTo(MainNavRoute.Album(album.name))
                        }
                    }
                    // TODO: Add pagination indicators
                }
            }
        }
        item {
            SongsListContainer(
                title = "Recently Added",
                subtitle = "Your most recent additions to the library",
                list = lastAdded,
                onPlay = play,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ListContainer(
                title = "Top Artists",
                subtitle = "Based on your listening history",
                actions = {
                    OutlinedButton(
                        onClick = {},
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(16.dp, 10.dp)
                    ) {
                        Text("View All")
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Icon(Icons.AutoMirrored.Default.ArrowForward, null)
                    }
                }
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                ) {
                    items(artists, { it.name }) { artist ->
                        ArtistCard(artist, Modifier.animateItem()) {
                            onArtistClick(artist)
                        }
                    }
                }
            }
        }
        item {
            SongsListContainer(
                title = "Recently Played",
                subtitle = "Your listening history",
                list = history,
                onPlay = play,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val artists = List(10) { Artist("Artist $it") }
    val albums = List(10) { Album("Album $it") }
    val songs = List(5) { Song(
        it.toLong(),
        Uri.EMPTY,
        "Song $it",
        "Song $it",
        "Artist $it",
        "Album $it",
    ) }

    Surface(Modifier.fillMaxSize()) {
        HomeScreen(
            albums = albums,
            artists = artists,
            lastAdded = songs,
            history = songs,
            onArtistClick = {},
            navigateTo = {},
            play = { _, _, _ -> },
            bottomPadding = 40.dp
        )
    }
}