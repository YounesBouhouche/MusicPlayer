package younesbouhouche.musicplayer.ui.components

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
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.events.ListsSortEvent
import younesbouhouche.musicplayer.events.PlaylistSortEvent
import younesbouhouche.musicplayer.events.SortEvent
import younesbouhouche.musicplayer.states.ColsCount
import younesbouhouche.musicplayer.states.ListSortState
import younesbouhouche.musicplayer.states.ListsSortType
import younesbouhouche.musicplayer.states.PlaylistSortState
import younesbouhouche.musicplayer.states.PlaylistSortType
import younesbouhouche.musicplayer.states.SortState
import younesbouhouche.musicplayer.states.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBar(
    modifier: Modifier = Modifier,
    state: SortState,
    onSortEvent: (SortEvent) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ExposedDropdownMenuBox(
            expanded = state.expanded,
            onExpandedChange = { onSortEvent(SortEvent.UpdateExpanded(it)) }
        ) {
            TextButton(
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = {
                    onSortEvent(SortEvent.Expand)
                }
            ) {
                Icon(
                    if (state.ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    null,
                    Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(state.sortType.label)
            }
            ExposedDropdownMenu(
                matchTextFieldWidth = false,
                expanded = state.expanded,
                onDismissRequest = { onSortEvent(SortEvent.Collapse) }
            ) {
                SortType.entries.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(it.label)
                        },
                        leadingIcon = {
                            Icon(it.icon, null)
                        },
                        onClick = {
                            onSortEvent(SortEvent.UpdateSortTypeOrToggleAsc(it))
                            onSortEvent(SortEvent.Collapse)
                        },
                        colors =
                            if (state.sortType == it) MenuDefaults.itemColors().copy(
                                textColor = MaterialTheme.colorScheme.primary,
                                leadingIconColor = MaterialTheme.colorScheme.primary
                            )
                            else MenuDefaults.itemColors()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsSortBar(
    modifier: Modifier = Modifier,
    state: ListSortState,
    onSortEvent: (ListsSortEvent) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ExposedDropdownMenuBox(
            expanded = state.expanded,
            onExpandedChange = { onSortEvent(ListsSortEvent.UpdateExpanded(it)) }
        ) {
            TextButton(
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = {
                    onSortEvent(ListsSortEvent.Expand)
                }
            ) {
                Icon(
                    if (state.ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    null,
                    Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(state.sortType.label)
            }
            ExposedDropdownMenu(
                matchTextFieldWidth = false,
                expanded = state.expanded,
                onDismissRequest = { onSortEvent(ListsSortEvent.Collapse) }
            ) {
                ListsSortType.entries.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(it.label)
                        },
                        leadingIcon = {
                            Icon(it.icon, null)
                        },
                        onClick = {
                            onSortEvent(ListsSortEvent.UpdateSortTypeOrToggleAsc(it))
                            onSortEvent(ListsSortEvent.Collapse)
                        },
                        colors =
                            if (state.sortType == it) MenuDefaults.itemColors().copy(
                                textColor = MaterialTheme.colorScheme.primary,
                                leadingIconColor = MaterialTheme.colorScheme.primary
                            )
                            else MenuDefaults.itemColors()
                    )
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = state.colsExpanded,
            onExpandedChange = { onSortEvent(ListsSortEvent.UpdateColsCountExpanded(it)) }
        ) {
            TextButton(
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = {
                    onSortEvent(ListsSortEvent.ExpandCols)
                }
            ) {
                Icon(
                    state.colsCount.icon,
                    null,
                    Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(state.colsCount.label)
            }
            ExposedDropdownMenu(
                matchTextFieldWidth = false,
                expanded = state.colsExpanded,
                onDismissRequest = { onSortEvent(ListsSortEvent.CollapseCols) }
            ) {
                ColsCount.entries.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(it.label)
                        },
                        leadingIcon = {
                            Icon(it.icon, null)
                        },
                        onClick = {
                            onSortEvent(ListsSortEvent.UpdateColsCount(it))
                            onSortEvent(ListsSortEvent.CollapseCols)
                        },
                        colors =
                            if (state.colsCount == it) MenuDefaults.itemColors().copy(
                                textColor = MaterialTheme.colorScheme.primary,
                                leadingIconColor = MaterialTheme.colorScheme.primary
                            )
                            else MenuDefaults.itemColors()
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSortBar(
    modifier: Modifier = Modifier,
    state: PlaylistSortState,
    onSortEvent: (PlaylistSortEvent) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ExposedDropdownMenuBox(
            expanded = state.expanded,
            onExpandedChange = { onSortEvent(PlaylistSortEvent.UpdateExpanded(it)) }
        ) {
            TextButton(
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = {
                    onSortEvent(PlaylistSortEvent.Expand)
                }
            ) {
                Icon(
                    if (state.ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    null,
                    Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(state.sortType.label)
            }
            ExposedDropdownMenu(
                matchTextFieldWidth = false,
                expanded = state.expanded,
                onDismissRequest = { onSortEvent(PlaylistSortEvent.Collapse) }
            ) {
                PlaylistSortType.entries.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(it.label)
                        },
                        leadingIcon = {
                            Icon(it.icon, null)
                        },
                        onClick = {
                            onSortEvent(PlaylistSortEvent.UpdateSortTypeOrToggleAsc(it))
                            onSortEvent(PlaylistSortEvent.Collapse)
                        },
                        colors =
                        if (state.sortType == it) MenuDefaults.itemColors().copy(
                            textColor = MaterialTheme.colorScheme.primary,
                            leadingIconColor = MaterialTheme.colorScheme.primary
                        )
                        else MenuDefaults.itemColors()
                    )
                }
            }
        }
    }
}
