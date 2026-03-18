package younesbouhouche.musicplayer.features.main.presentation.routes.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.features.main.presentation.navigation.MainNavRoute

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    onArtistClick: (Artist) -> Unit,
    navigateTo: (MainNavRoute) -> Unit,
) {
    val homeViewModel = koinViewModel<HomeViewModel>()
    val albums by homeViewModel.albums.collectAsState()
    val artists by homeViewModel.artists.collectAsState()
    val lastAdded by homeViewModel.lastAdded.collectAsState()
    val history by homeViewModel.history.collectAsState()

    HomeScreen(
        modifier = modifier,
        bottomPadding = bottomPadding,
        albums = albums,
        artists = artists,
        lastAdded = lastAdded,
        history = history,
        onArtistClick = onArtistClick,
        navigateTo = navigateTo,
        play = homeViewModel::play
    )
}

