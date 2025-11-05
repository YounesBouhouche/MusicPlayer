package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.main.presentation.components.GridScreen
import younesbouhouche.musicplayer.main.presentation.components.ListItem
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import younesbouhouche.musicplayer.main.presentation.components.PictureCard
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortBottomSheet
import younesbouhouche.musicplayer.main.presentation.util.SortState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArtistsScreen(
    artists: List<Artist>,
    sortState: SortState<ListsSortType>,
    onSortStateChange: (SortState<ListsSortType>) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (Artist) -> Unit
) {
    GridScreen(
        artists,
        sortState,
        {
            onSortStateChange(sortState.copy(expanded = true))
        },
        {
            ListItem(
                headline = it.name,
                supporting = pluralStringResource(
                    R.plurals.item_s,
                    it.items.size,
                    it.items.size
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
        PaddingValues(bottom = 260.dp)
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
        onSortStateChange = onSortStateChange,
    )
}