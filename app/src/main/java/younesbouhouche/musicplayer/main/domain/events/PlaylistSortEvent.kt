package younesbouhouche.musicplayer.main.domain.events

import younesbouhouche.musicplayer.main.presentation.states.PlaylistSortType

sealed interface PlaylistSortEvent {
    data class UpdateSortType(val sortType: PlaylistSortType) : PlaylistSortEvent
    data class UpdateSortTypeOrToggleAsc(val sortType: PlaylistSortType) : PlaylistSortEvent
    data object Expand: PlaylistSortEvent
    data object Collapse: PlaylistSortEvent
    data class UpdateExpanded(val expanded: Boolean) : PlaylistSortEvent
    data object ToggleAscending: PlaylistSortEvent
    data class UpdateAscending(val ascending: Boolean): PlaylistSortEvent
}