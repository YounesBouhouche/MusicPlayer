package younesbouhouche.musicplayer.features.main.presentation.routes.artist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.features.main.presentation.components.GridScreen
import younesbouhouche.musicplayer.features.main.presentation.components.ListItem
import younesbouhouche.musicplayer.features.main.presentation.components.PictureCard
import younesbouhouche.musicplayer.features.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.features.main.presentation.util.SortBottomSheet
import younesbouhouche.musicplayer.features.main.presentation.routes.artist.ArtistsViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArtistsScreen(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    onClick: (Artist) -> Unit
) {
    val viewModel = koinViewModel<ArtistsViewModel>()
    val artists by viewModel.artists.collectAsStateWithLifecycle()
    val sortState by viewModel.sortState.collectAsStateWithLifecycle()

    GridScreen(
        artists,
        sortState,
        {
            viewModel.setSortState(sortState.copy(expanded = true))
        },
        {
            ListItem(
                headline = it.name,
                supporting = pluralStringResource(
                    R.plurals.item_s,
                    it.songs.size,
                    it.songs.size
                ),
                cover = it.getPicture(),
                icon = Icons.Default.Person,
                modifier = Modifier.animateItem()
            ) {
                onClick(it)
            }
        },
        { artist ->
            PictureCard(
                artist.getPicture(),
                Icons.Default.Person,
                {
                    onClick(artist)
                },
                Modifier.animateItem()
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                ) {
                    Text(
                        artist.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        modifier,
        { it.name },
        PaddingValues(bottom = bottomPadding),
    )
    SortBottomSheet(
        sortState,
        options = ListsSortType.entries,
        icon = {
            it.icon
        },
        text = {
            it.label
        },
        onSortStateChange = viewModel::setSortState,
    )
}