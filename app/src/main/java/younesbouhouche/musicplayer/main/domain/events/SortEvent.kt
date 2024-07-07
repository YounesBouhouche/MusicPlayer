package younesbouhouche.musicplayer.main.domain.events

import younesbouhouche.musicplayer.main.presentation.states.SortType

sealed interface SortEvent {
    data class UpdateSortType(val sortType: SortType) : SortEvent
    data class UpdateSortTypeOrToggleAsc(val sortType: SortType) : SortEvent
    data object Expand: SortEvent
    data object Collapse: SortEvent
    data class UpdateExpanded(val expanded: Boolean) : SortEvent
    data object ToggleAscending: SortEvent
    data class UpdateAscending(val ascending: Boolean): SortEvent
}

