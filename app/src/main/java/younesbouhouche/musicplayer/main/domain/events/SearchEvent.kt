package younesbouhouche.musicplayer.main.domain.events

import younesbouhouche.musicplayer.core.domain.models.SearchFilter

sealed interface SearchEvent {
    data object ClearQuery : SearchEvent

    data class UpdateQuery(val query: String) : SearchEvent

    data class UpdateExpanded(val expanded: Boolean) : SearchEvent
    data class UpdateResultExpanded(
        val files: Boolean? = null,
        val artists: Boolean? = null,
        val albums: Boolean? = null,
        val playlists: Boolean? = null,
        ) : SearchEvent

    data object Expand : SearchEvent

    data object Collapse : SearchEvent

    data class ToggleFilter(val filter: SearchFilter): SearchEvent
}
