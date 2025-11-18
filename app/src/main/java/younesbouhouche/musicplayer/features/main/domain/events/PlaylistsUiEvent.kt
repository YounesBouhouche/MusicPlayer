package younesbouhouche.musicplayer.features.main.domain.events

import younesbouhouche.musicplayer.features.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.features.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.features.main.presentation.util.SortState

sealed interface PlaylistsUiEvent {
    data class ShowBottomSheet(val id: Int) : PlaylistsUiEvent

    data object HidePlaylistBottomSheet : PlaylistsUiEvent

    data class SetSortState(val state: SortState<ListsSortType>) : PlaylistsUiEvent

    data class SetPlaylistSortState(val state: SortState<PlaylistSortType>) : PlaylistsUiEvent

    data class SetSheetVisible(val visible: Boolean) : PlaylistsUiEvent

}