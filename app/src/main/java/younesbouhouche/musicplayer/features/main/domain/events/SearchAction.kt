package younesbouhouche.musicplayer.features.main.domain.events

import younesbouhouche.musicplayer.features.main.presentation.SearchFilter

sealed interface SearchAction {
    data object ClearQuery : SearchAction

    data class UpdateQuery(val query: String) : SearchAction

    data class UpdateResultExpanded(
        val files: Boolean? = null,
        val artists: Boolean? = null,
        val albums: Boolean? = null,
        val playlists: Boolean? = null,
        ) : SearchAction

    data class ToggleFilter(val filter: SearchFilter): SearchAction
}
