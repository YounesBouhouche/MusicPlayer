package younesbouhouche.musicplayer.main.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import younesbouhouche.musicplayer.core.domain.models.SearchFilter
import younesbouhouche.musicplayer.core.domain.util.stateInVM
import younesbouhouche.musicplayer.main.domain.events.SearchEvent
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.main.domain.repo.PlaylistRepository
import younesbouhouche.musicplayer.main.presentation.states.SearchState
import younesbouhouche.musicplayer.main.presentation.util.search

class SearchVM(
    mediaRepository: MediaRepository,
    playlistRepository: PlaylistRepository
): ViewModel() {
    private fun <T> Flow<T>.stateInVM(initialValue: T) = stateInVM(initialValue, viewModelScope)
    private val _files = mediaRepository.getAllMedia().stateInVM(emptyList())
    private val _artists = mediaRepository.getArtists().stateInVM(emptyList())
    private val _albums = mediaRepository.getAlbums().stateInVM(emptyList())
    private val _playlists = playlistRepository.getAllPlaylists().stateInVM(emptyList())
    private val _searchState = MutableStateFlow(SearchState())
    private val _searchQuery = _searchState.map { it.query }
    private val _searchFilters = _searchState.map { it.result.filters }
    val searchState = combine(_searchQuery, _searchFilters, _playlists) { _, filters, playlists ->
        val files = _files.first()
        val state = _searchState.first()
        val artists = _artists.first()
        val albums = _albums.first()
        val result =
            if (state.query.isNotBlank()) {
                val filesResult =
                    if (SearchFilter.FILES in filters)
                        files.filter { it.search(state.query) }
                    else emptyList()
                val artistsResult =
                    if (SearchFilter.ARTISTS in filters)
                        artists.filter { it.search(state.query) }
                    else emptyList()
                val albumsResult =
                    if (SearchFilter.ALBUMS in filters)
                        albums.filter { it.search(state.query) }
                    else emptyList()
                val playlistsResult =
                    if (SearchFilter.PLAYLISTS in filters)
                        playlists.filter { it.search(state.query) }
                    else emptyList()
                state.result.copy(
                    files = filesResult,
                    artists = artistsResult,
                    albums = albumsResult,
                    playlists = playlistsResult
                )
            } else {
                state.result.copy(
                    files = emptyList(),
                    artists = emptyList(),
                    albums = emptyList(),
                    playlists = emptyList()
                )
            }
        state.copy(result = result)
    }.stateInVM(SearchState())

    fun onSearchEvent(event: SearchEvent) {
        when (event) {
            SearchEvent.ClearQuery -> {
                _searchState.update {
                    it.copy(query = "")
                }
            }

            is SearchEvent.UpdateQuery -> {
                _searchState.update {
                    it.copy(query = event.query)
                }
            }

            is SearchEvent.ToggleFilter -> {
                _searchState.update { state ->
                    state.copy(result = state.result.toggleFilter(event.filter))
                }
            }

            is SearchEvent.UpdateResultExpanded -> {
                _searchState.update { state ->
                    state.copy(
                        filesExpanded = event.files ?: state.filesExpanded,
                        artistsExpanded = event.artists ?: state.artistsExpanded,
                        albumsExpanded = event.albums ?: state.albumsExpanded,
                        playlistsExpanded = event.playlists ?: state.playlistsExpanded
                    )
                }
            }
        }
    }
}