package younesbouhouche.musicplayer.features.main.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.presentation.ColsCount
import younesbouhouche.musicplayer.features.main.presentation.util.composables.TitleText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T>SortBottomSheet(
    sortState: SortState<T>,
    options: List<T>,
    icon: @Composable (T) -> ImageVector,
    text: @Composable (T) -> Int,
    onSortStateChange: (SortState<T>) -> Unit,
    modifier: Modifier = Modifier
) {
    if (sortState.expanded)
        ModalBottomSheet(
            { onSortStateChange(sortState.copy(expanded = false)) },
            modifier,
            contentWindowInsets = {
                BottomSheetDefaults.windowInsets.add(WindowInsets(bottom = 16.dp))
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SheetItem(
                    stringResource(R.string.sort_by),
                    options,
                    sortState.sortType,
                    { onSortStateChange(sortState.copy(sortType = it)) },
                    icon,
                    text
                )
                SheetItem(
                    stringResource(R.string.order),
                    listOf(0, 1),
                    sortState.sortType,
                    { onSortStateChange(sortState.copy(ascending = it == 0)) },
                    {
                        if (it == 0) Icons.Default.ArrowUpward
                        else Icons.Default.ArrowDownward
                    },
                    {
                        if (it == 0) R.string.ascending
                        else R.string.descending
                    }
                )
                sortState.colsCount?.let { colsCount ->
                    SheetItem(
                        stringResource(R.string.grid),
                        ColsCount.entries,
                        colsCount,
                        { onSortStateChange(sortState.copy(colsCount = it)) },
                        { item -> item.icon },
                        { item -> item.label }
                    )
                }
            }
        }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun <T> SheetItem(
    title: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelect: (T) -> Unit,
    icon: @Composable (T) -> ImageVector,
    text: @Composable (T) -> Int,
) {
    TitleText(title)
    LazyRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            ButtonGroupDefaults.ConnectedSpaceBetween,
            Alignment.CenterHorizontally
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(options) { index, option ->
            ToggleButton(
                selectedOption == option,
                {
                    onOptionSelect(option)
                },
                Modifier.height(ButtonDefaults.MediumContainerHeight),
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.size - 1 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight)
            ) {
                Icon(
                    icon(option),
                    null,
                    Modifier.size(ButtonDefaults.MediumIconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.MediumIconSpacing))
                Text(stringResource(text(option)))
            }
        }
    }
}