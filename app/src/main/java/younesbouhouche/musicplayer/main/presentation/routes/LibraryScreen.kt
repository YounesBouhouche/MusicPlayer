package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveButton
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.main.presentation.components.MusicCardBottomSheet
import younesbouhouche.musicplayer.main.presentation.components.MusicCardListItem
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
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
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp, 12.dp, 12.dp, 200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                Modifier.padding(top = 6.dp, bottom = 16.dp, end = 6.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                ExpressiveIconButton(
                    icon = Icons.AutoMirrored.Filled.Sort,
                    onClick = {
                        onSortStateChange(sortState.copy(expanded = true))
                    },
                    size = IconButtonDefaults.mediumIconSize,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(),
                )
            }
        }
        itemsIndexed(files, { index, it -> it.id }) { index, it ->
            MusicCardListItem(
                it,
                shape = expressiveRectShape(index, files.size),
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