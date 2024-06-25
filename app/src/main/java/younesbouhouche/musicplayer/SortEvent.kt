package younesbouhouche.musicplayer

import younesbouhouche.musicplayer.states.PlaylistSortState
import younesbouhouche.musicplayer.states.PlaylistSortType
import younesbouhouche.musicplayer.states.SortState
import younesbouhouche.musicplayer.states.SortType

sealed interface SortEvent {
    data class UpdateSortType(val sortType: SortType) : SortEvent
    data class UpdateSortTypeOrToggleAsc(val sortType: SortType) : SortEvent
    data object Expand: SortEvent
    data object Collapse: SortEvent
    data class UpdateExpanded(val expanded: Boolean) : SortEvent
    data object ToggleAscending: SortEvent
    data class UpdateAscending(val ascending: Boolean): SortEvent
}

sealed interface PlaylistSortEvent {
    data class UpdateSortType(val sortType: PlaylistSortType) : PlaylistSortEvent
    data class UpdateSortTypeOrToggleAsc(val sortType: PlaylistSortType) : PlaylistSortEvent
    data object Expand: PlaylistSortEvent
    data object Collapse: PlaylistSortEvent
    data class UpdateExpanded(val expanded: Boolean) : PlaylistSortEvent
    data object ToggleAscending: PlaylistSortEvent
    data class UpdateAscending(val ascending: Boolean): PlaylistSortEvent
}