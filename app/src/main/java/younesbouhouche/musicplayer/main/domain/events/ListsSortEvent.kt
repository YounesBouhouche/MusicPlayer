package younesbouhouche.musicplayer.main.domain.events

import younesbouhouche.musicplayer.main.domain.models.ColsCount
import younesbouhouche.musicplayer.main.domain.models.ListsSortType

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