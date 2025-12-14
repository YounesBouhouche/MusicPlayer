package younesbouhouche.musicplayer.features.main.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository
import younesbouhouche.musicplayer.core.domain.repositories.PlaylistRepository
import younesbouhouche.musicplayer.features.main.presentation.SearchFilter
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.events.SearchAction
import younesbouhouche.musicplayer.features.main.presentation.states.SearchState
import younesbouhouche.musicplayer.features.main.presentation.util.search

class SearchVM(
    val mainViewModel: MainViewModel,
    mediaRepository: MusicRepository,
    playlistRepository: PlaylistRepository
): ViewModel() {
    private fun <T> Flow<T>.stateInVM(initialValue: T) = stateInVM(initialValue, viewModelScope)
    private val _files = mediaRepository.getSongsList().stateInVM(emptyList())
    private val _artists = mediaRepository.getArtists().stateInVM(emptyList())
    private val _albums = mediaRepository.getAlbums().stateInVM(emptyList())
    private val _playlists = playlistRepository.getPlaylists().stateInVM(emptyList())
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

    fun play(
        tracks: List<Long>,
        index: Int,
    ) = mainViewModel.play(
        tracks = tracks,
        index = index,
        shuffle = false,
    )

    fun onAction(event: SearchAction) {
        when (event) {
            SearchAction.ClearQuery -> {
                _searchState.update {
                    it.copy(query = "")
                }
            }

            is SearchAction.UpdateQuery -> {
                _searchState.update {
                    it.copy(query = event.query)
                }
            }

            is SearchAction.ToggleFilter -> {
                _searchState.update { state ->
                    state.copy(result = state.result.toggleFilter(event.filter))
                }
            }

            is SearchAction.UpdateResultExpanded -> {
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