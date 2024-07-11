package younesbouhouche.musicplayer.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.main.domain.events.ListsSortEvent
import younesbouhouche.musicplayer.main.domain.models.ColsCount
import younesbouhouche.musicplayer.main.domain.models.ListsSortType
import younesbouhouche.musicplayer.main.presentation.states.ListSortState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsSortBar(
    modifier: Modifier = Modifier,
    state: ListSortState,
    onSortEvent: (ListsSortEvent) -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ExposedDropdownMenuBox(
            expanded = state.expanded,
            onExpandedChange = { onSortEvent(ListsSortEvent.UpdateExpanded(it)) },
        ) {
            TextButton(
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                colors =
                    ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                onClick = {
                    onSortEvent(ListsSortEvent.Expand)
                },
            ) {
                Icon(
                    if (state.ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    null,
                    Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(stringResource(state.sortType.label))
            }
            ExposedDropdownMenu(
                matchTextFieldWidth = false,
                expanded = state.expanded,
                onDismissRequest = { onSortEvent(ListsSortEvent.Collapse) },
            ) {
                ListsSortType.entries.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(it.label))
                        },
                        leadingIcon = {
                            Icon(it.icon, null)
                        },
                        onClick = {
                            onSortEvent(ListsSortEvent.UpdateSortTypeOrToggleAsc(it))
                            onSortEvent(ListsSortEvent.Collapse)
                        },
                        colors =
                            if (state.sortType == it) {
                                MenuDefaults.itemColors().copy(
                                    textColor = MaterialTheme.colorScheme.primary,
                                    leadingIconColor = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                MenuDefaults.itemColors()
                            },
                    )
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = state.colsExpanded,
            onExpandedChange = { onSortEvent(ListsSortEvent.UpdateColsCountExpanded(it)) },
        ) {
            TextButton(
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                colors =
                    ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                onClick = {
                    onSortEvent(ListsSortEvent.ExpandCols)
                },
            ) {
                Icon(
                    state.colsCount.icon,
                    null,
                    Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(stringResource(state.colsCount.label))
            }
            ExposedDropdownMenu(
                matchTextFieldWidth = false,
                expanded = state.colsExpanded,
                onDismissRequest = { onSortEvent(ListsSortEvent.CollapseCols) },
            ) {
                ColsCount.entries.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(it.label))
                        },
                        leadingIcon = {
                            Icon(it.icon, null)
                        },
                        onClick = {
                            onSortEvent(ListsSortEvent.UpdateColsCount(it))
                            onSortEvent(ListsSortEvent.CollapseCols)
                        },
                        colors =
                            if (state.colsCount == it) {
                                MenuDefaults.itemColors().copy(
                                    textColor = MaterialTheme.colorScheme.primary,
                                    leadingIconColor = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                MenuDefaults.itemColors()
                            },
                    )
                }
            }
        }
    }
}
