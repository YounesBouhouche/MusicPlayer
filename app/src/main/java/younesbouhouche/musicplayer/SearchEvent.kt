package younesbouhouche.musicplayer

sealed interface SearchEvent {
    data object ClearQuery: SearchEvent
    data class UpdateQuery(val query: String): SearchEvent
    data class UpdateExpanded(val expanded: Boolean): SearchEvent
    data object Expand: SearchEvent
    data object Collapse: SearchEvent
}