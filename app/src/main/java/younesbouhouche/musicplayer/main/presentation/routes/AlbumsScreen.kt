package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.main.presentation.components.GridScreen
import younesbouhouche.musicplayer.main.presentation.components.ListItem
import younesbouhouche.musicplayer.main.presentation.components.PictureCard
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortBottomSheet
import younesbouhouche.musicplayer.main.presentation.util.SortState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlbumsScreen(
    albums: List<Album>,
    sortState: SortState<ListsSortType>,
    onSortStateChange: (SortState<ListsSortType>) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (Album) -> Unit
) {
    GridScreen(
        albums,
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
                cover = it.cover,
                icon = Icons.Default.Album
            ) {
                onClick(it)
            }
        },
        { album ->
            PictureCard(
                album.cover,
                Icons.Default.Album,
                {
                    onClick(album)
                },
                Modifier.animateItem()
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                ) {
                    Text(
                        album.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        modifier,
        { it.name },
        contentPadding = PaddingValues(bottom = 260.dp)
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