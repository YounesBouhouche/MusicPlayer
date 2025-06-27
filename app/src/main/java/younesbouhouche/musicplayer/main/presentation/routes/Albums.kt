package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.main.presentation.components.ItemsLazyVerticalGrid
import younesbouhouche.musicplayer.main.presentation.components.ListsSortSheet
import younesbouhouche.musicplayer.main.presentation.components.MyCard
import younesbouhouche.musicplayer.main.presentation.components.MyListItem
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.Albums(
    albums: List<Album>,
    onClick: (String) -> Unit,
    onLongClick: (Album) -> Unit,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    sortState: SortState<ListsSortType>,
    onSortStateChange: (SortState<ListsSortType>) -> Unit,
    onPlayerEvent: (PlayerEvent) -> Unit,
) {
    ItemsLazyVerticalGrid(
        items = albums,
        itemKey = { _, it -> it.name },
        gridCount = sortState.colsCount?.count ?: 1,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(12.dp),
        singleLineItemContent = { index, it ->
            MyListItem(
                onClick = { onClick(it.name) },
                onLongClick = { onLongClick(it) },
                headline = it.name,
                supporting = pluralStringResource(R.plurals.item_s, it.items.size, it.items.size),
                cover = it.cover,
                alternative = Icons.Default.Album,
                modifier = Modifier.animateItem(),
                animatedContentScope = animatedContentScope,
                key = "album-${it.name}",
                shape = listItemShape(index, albums.size),
                trailingContent = {
                    IconButton(onClick = { onPlayerEvent(PlayerEvent.PlayIds(it.items)) }) {
                        Icon(Icons.Outlined.PlayArrow, null)
                    }
                    IconButton(onClick = { onLongClick(it) }) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                },
                background = MaterialTheme.colorScheme.surfaceContainerLow,
            )
        },
        itemContent = { _, it ->
            MyCard(
                modifier = Modifier.animateItem(),
                text = it.name,
                cover = it.cover,
                alternative = Icons.Default.Album,
                onClick = { onClick(it.name) },
                onLongClick = { onLongClick(it) },
                animatedContentScope = animatedContentScope,
                key = "album-${it.name}",
            )
        }
    )
    ListsSortSheet(sortState) { onSortStateChange(it) }
}
