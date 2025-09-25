package younesbouhouche.musicplayer.main.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.ColsCount
import younesbouhouche.musicplayer.main.presentation.util.composables.TitleText

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
                BottomSheetDefaults.windowInsets.add(WindowInsets(16.dp, 0.dp, 16.dp, 16.dp))
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TitleText(stringResource(R.string.sort_by))
                LazyRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween, Alignment.CenterHorizontally)
                ) {
                    itemsIndexed(options) { index, option ->
                        ToggleButton(
                            sortState.sortType == option,
                            {
                                onSortStateChange(sortState.copy(sortType = option))
                            },
                            Modifier
                                .weight(1f)
                                .height(ButtonDefaults.MediumContainerHeight),
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
                TitleText(stringResource(R.string.order))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                ) {
                    repeat(2) { index ->
                        ToggleButton(
                            if (index == 0) sortState.ascending
                            else !sortState.ascending,
                            {
                                onSortStateChange(
                                    sortState.copy(ascending = index == 0)
                                )
                            },
                            Modifier
                                .weight(1f)
                                .height(ButtonDefaults.MediumContainerHeight),
                            shapes =
                                if (index == 0) ButtonGroupDefaults.connectedLeadingButtonShapes()
                                else ButtonGroupDefaults.connectedTrailingButtonShapes(),
                            contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight)
                        ) {
                            Icon(
                                if (index == 0) Icons.Default.ArrowUpward
                                else Icons.Default.ArrowDownward,
                                null,
                                Modifier.size(ButtonDefaults.MediumIconSize)
                            )
                            Spacer(Modifier.width(ButtonDefaults.MediumIconSpacing))
                            Text(
                                stringResource(
                                    if (index == 0) R.string.ascending
                                    else R.string.descending
                                )
                            )
                        }
                    }
                }
                sortState.colsCount?.let { colsCount ->
                    TitleText(stringResource(R.string.grid))
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                    ) {
                        ColsCount.entries.forEachIndexed { index, item ->
                            ToggleButton(
                                item == colsCount,
                                {
                                    onSortStateChange(sortState.copy(colsCount = item))
                                },
                                Modifier
                                    .weight(1f)
                                    .height(ButtonDefaults.MediumContainerHeight),
                                shapes = when(index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    ColsCount.entries.size - 1 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight)
                            ) {
                                Icon(
                                    item.icon,
                                    null,
                                    Modifier.size(ButtonDefaults.MediumIconSize)
                                )
                                Spacer(Modifier.width(ButtonDefaults.MediumIconSpacing))
                                Text(stringResource(item.label))
                            }
                        }
                    }
                }
            }
        }
}