package younesbouhouche.musicplayer.events

import younesbouhouche.musicplayer.states.ColsCount
import younesbouhouche.musicplayer.states.ListsSortType

sealed interface ListsSortEvent {
    data class UpdateSortType(val sortType: ListsSortType) : ListsSortEvent
    data class UpdateSortTypeOrToggleAsc(val sortType: ListsSortType) : ListsSortEvent
    data object Expand: ListsSortEvent
    data object Collapse: ListsSortEvent
    data class UpdateExpanded(val expanded: Boolean) : ListsSortEvent
    data object ToggleAscending: ListsSortEvent
    data class UpdateAscending(val ascending: Boolean): ListsSortEvent
    data object ExpandCols: ListsSortEvent
    data object CollapseCols: ListsSortEvent
    data class UpdateColsCount(val colsCount: ColsCount) : ListsSortEvent
    data class UpdateColsCountExpanded(val expanded: Boolean) : ListsSortEvent
}