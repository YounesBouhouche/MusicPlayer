package younesbouhouche.musicplayer.features.main.presentation.states

import younesbouhouche.musicplayer.features.main.presentation.SearchFilter
import younesbouhouche.musicplayer.features.main.presentation.SearchResult

data class SearchState(
    val query: String = "",
    val filesExpanded: Boolean = false,
    val artistsExpanded: Boolean = false,
    val albumsExpanded: Boolean = false,
    val playlistsExpanded: Boolean = false,
    val result: SearchResult = SearchResult(setOf(SearchFilter.FILES)),
    val expanded: Boolean = false,
)

val SearchState.isEmpty: Boolean
    get() = result.files.takeIf { result.filters.contains(SearchFilter.FILES) }?.isEmpty() != false &&
            result.artists.takeIf { result.filters.contains(SearchFilter.ARTISTS) }?.isEmpty() != false &&
            result.albums.takeIf { result.filters.contains(SearchFilter.ALBUMS) }?.isEmpty() != false &&
            result.playlists.takeIf { result.filters.contains(SearchFilter.PLAYLISTS) }?.isEmpty() != false