package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.main.presentation.components.ItemsLazyVerticalGrid
import younesbouhouche.musicplayer.main.presentation.components.ListsSortSheet
import younesbouhouche.musicplayer.main.presentation.components.MyCard
import younesbouhouche.musicplayer.main.presentation.components.MyListItem
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.Artists(
    artists: List<Artist>,
    onClick: (String) -> Unit,
    onLongClick: (Artist) -> Unit,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    sortState: SortState<ListsSortType>,
    onSortStateChange: (SortState<ListsSortType>) -> Unit,
    onPlayerEvent: (PlayerEvent) -> Unit,
) {
    ItemsLazyVerticalGrid(
        items = artists,
        itemKey = { it.name },
        gridCount = sortState.colsCount?.count ?: 1,
        modifier = modifier,
        singleLineItemContent = {
            MyListItem(
                onClick = { onClick(it.name) },
                onLongClick = { onLongClick(it) },
                headline = it.name,
                supporting = pluralStringResource(R.plurals.item_s, it.items.size, it.items.size),
                cover = it.getPicture(),
                alternative = Icons.Default.Person,
                modifier = Modifier.animateItem(),
                trailingContent = {
                    IconButton(onClick = { onPlayerEvent(PlayerEvent.PlayIds(it.items)) }) {
                        Icon(Icons.Outlined.PlayArrow, null)
                    }
                    IconButton(onClick = { onLongClick(it) }) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                },
                animatedContentScope = animatedContentScope,
                key = "artist-${it.name}"
            )
        },
        itemContent = {
            MyCard(
                modifier = Modifier.animateItem(),
                text = it.name,
                cover = it.getPicture(),
                alternative = Icons.Default.Person,
                onClick = { onClick(it.name) },
                onLongClick = { onLongClick(it) },
                animatedContentScope = animatedContentScope,
                key = "artist-${it.name}"
                ,
            )
        }
    )
    ListsSortSheet(sortState) { onSortStateChange(it) }
}
