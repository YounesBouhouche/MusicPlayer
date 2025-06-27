package younesbouhouche.musicplayer.main.presentation.states

import younesbouhouche.musicplayer.core.domain.models.SearchFilter
import younesbouhouche.musicplayer.core.domain.models.SearchResult

data class SearchState(
    val query: String = "",
    val filesExpanded: Boolean = false,
    val artistsExpanded: Boolean = false,
    val albumsExpanded: Boolean = false,
    val playlistsExpanded: Boolean = false,
    val result: SearchResult = SearchResult(setOf(SearchFilter.FILES)),
    val expanded: Boolean = false,
)
