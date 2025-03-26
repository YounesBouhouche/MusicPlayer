package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.pluralStringResource
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.main.presentation.components.ItemsLazyVerticalGrid
import younesbouhouche.musicplayer.main.presentation.components.ListsSortSheet
import younesbouhouche.musicplayer.main.presentation.components.MyCard
import younesbouhouche.musicplayer.main.presentation.components.MyListItem
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState

@Composable
fun Albums(
    albums: List<Album>,
    onClick: (Album) -> Unit,
    onLongClick: (Album) -> Unit,
    modifier: Modifier = Modifier,
    sortState: SortState<ListsSortType>,
    onSortStateChange: (SortState<ListsSortType>) -> Unit,
    onPlayerEvent: (PlayerEvent) -> Unit,
) {
    ItemsLazyVerticalGrid(
        items = albums,
        itemKey = { it.title },
        gridCount = sortState.colsCount?.count ?: 1,
        modifier = modifier,
        singleLineItemContent = {
            MyListItem(
                onClick = { onClick(it) },
                onLongClick = { onLongClick(it) },
                headline = it.title,
                supporting = pluralStringResource(R.plurals.item_s, it.items.size, it.items.size),
                cover = it.cover?.asImageBitmap(),
                alternative = Icons.Default.Album,
                modifier = Modifier.animateItem(),
                trailingContent = {
                    IconButton(onClick = { onPlayerEvent(PlayerEvent.PlayIds(it.items)) }) {
                        Icon(Icons.Outlined.PlayArrow, null)
                    }
                    IconButton(onClick = { onLongClick(it) }) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                },
            )
        },
        itemContent = {
            MyCard(
                modifier = Modifier.animateItem(),
                text = it.title,
                cover = it.cover,
                alternative = Icons.Default.Album,
                onClick = { onClick(it) },
                onLongClick = { onLongClick(it) },
            )
        }
    )
    ListsSortSheet(sortState) { onSortStateChange(it) }
}
