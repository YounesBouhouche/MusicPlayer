package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.main.presentation.components.EmptyContainer
import younesbouhouche.musicplayer.main.presentation.components.MusicCardListItem
import younesbouhouche.musicplayer.main.presentation.util.SortBottomSheet
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType
import younesbouhouche.musicplayer.main.presentation.util.expressiveRectShape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibraryScreen(
    files: List<MusicCard>,
    sortState: SortState<SortType>,
    onSortStateChange: (SortState<SortType>) -> Unit,
    modifier: Modifier = Modifier,
    onShowBottomSheet: (MusicCard) -> Unit = {},
    onClick: (MusicCard) -> Unit = {},
) {
    val favoriteFiles = files.filter { it.favorite }
    var isFavoritesVisible by remember { mutableStateOf(true) }
    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(2) { index ->
                val selected = index == 0 == isFavoritesVisible
                val colors =
                    if (index == 0)
                        ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    else
                        ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            checkedContainerColor = MaterialTheme.colorScheme.errorContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                val icon = when (index) {
                    0 -> Icons.Default.LibraryMusic
                    else -> Icons.Default.Favorite
                }
                val text = if (index == 0) R.string.all else R.string.favorites
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val weight by animateFloatAsState(if (pressed) 1.5f else 1f)
                ToggleButton(
                    checked = selected,
                    onCheckedChange = {
                        isFavoritesVisible = index == 0
                    },
                    modifier = Modifier
                        .weight(weight)
                        .height(ButtonDefaults.MediumContainerHeight),
                    shapes =
                        if (index == 0) ButtonGroupDefaults.connectedLeadingButtonShapes()
                        else ButtonGroupDefaults.connectedTrailingButtonShapes(),
                    contentPadding = ButtonDefaults
                        .contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                    interactionSource = interactionSource,
                    colors = colors
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        Modifier.size(ButtonDefaults.MediumIconSize)
                    )
                    Spacer(Modifier.width(ButtonDefaults.MediumIconSpacing))
                    Text(
                        stringResource(text),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            ExpressiveIconButton(
                icon = Icons.AutoMirrored.Filled.Sort,
                onClick = {
                    onSortStateChange(sortState.copy(expanded = true))
                },
                size = IconButtonDefaults.mediumIconSize,
                colors = IconButtonDefaults.filledTonalIconButtonColors(),
            )
        }
        AnimatedContent(
            isFavoritesVisible,
            Modifier
                .weight(1f)
                .fillMaxSize(),
            transitionSpec = {
                if (initialState)
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                else
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
            }
        ) {
            if (it) {
                ItemsList(
                    modifier = modifier,
                    items = files,
                    onShowBottomSheet = onShowBottomSheet,
                    onClick = onClick,
                    emptyIcon = Icons.Default.LibraryMusic,
                    emptyText = stringResource(R.string.your_library_is_empty)
                )
            } else {
                ItemsList(
                    modifier = modifier,
                    items = favoriteFiles,
                    onShowBottomSheet = onShowBottomSheet,
                    onClick = onClick,
                    emptyIcon = Icons.Default.Favorite,
                    emptyText = stringResource(R.string.empty_favorites_text)
                )
            }
        }
    }
    SortBottomSheet(
        sortState,
        options = SortType.entries,
        icon = {
            it.icon
        },
        text = {
            it.label
        },
        onSortStateChange = onSortStateChange,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ItemsList(
    modifier: Modifier = Modifier,
    items: List<MusicCard>,
    emptyIcon: ImageVector,
    emptyText: String,
    onShowBottomSheet: (MusicCard) -> Unit = {},
    onClick: (MusicCard) -> Unit = {},
) {
    EmptyContainer(
        items.isEmpty(),
        emptyIcon,
        emptyText
    ) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(12.dp, 12.dp, 12.dp, 260.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items, { index, it -> it.id }) { index, it ->
                MusicCardListItem(
                    it,
                    modifier = Modifier.animateItem(),
                    shape = expressiveRectShape(index, items.size),
                    trailingContent = {
                        ExpressiveIconButton(
                            Icons.Default.MoreVert,
                            widthOption = IconButtonDefaults.IconButtonWidthOption.Narrow,
                            size = IconButtonDefaults.mediumIconSize,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            onShowBottomSheet(it)
                        }
                    }
                ) {
                    onClick(it)
                }
            }
        }
    }
}