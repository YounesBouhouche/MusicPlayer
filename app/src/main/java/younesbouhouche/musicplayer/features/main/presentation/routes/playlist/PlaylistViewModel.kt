package younesbouhouche.musicplayer.features.main.presentation.routes.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetPlaylistUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.SetPlaylistSongsUseCase
import younesbouhouche.musicplayer.features.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.main.util.sortBy

class PlaylistViewModel(
    val mainViewModel: MainViewModel,
    getPlaylistUseCase: GetPlaylistUseCase,
    val setPlaylistSongsUseCase: SetPlaylistSongsUseCase,
    playlistId: Long
): ViewModel() {
    private val _sortState = MutableStateFlow(SortState(PlaylistSortType.Custom))
    val sortState = _sortState.asStateFlow()
    private val _playlist = getPlaylistUseCase(playlistId)
    val playlist = combine(_playlist, _sortState) { playlist, sortState ->
        playlist.copy(songs = playlist.songs.sortBy(sortState.sortType, sortState.ascending))
    }.stateInVM(Playlist(id = playlistId), viewModelScope)

    fun play(
        tracks: List<Long>,
        index: Int = 0,
        shuffle: Boolean = false
    ) = mainViewModel.play(tracks, index, shuffle)

    fun setSortState(sortState: SortState<PlaylistSortType>) {
        _sortState.value = sortState
    }

    fun update(newList: List<Long>) {
        viewModelScope.launch {
            println("Updating from ${_playlist.first().songs.map { it.id }}")
            println("Updating to $newList")
            setPlaylistSongsUseCase(_playlist.first().id, newList)
        }
    }
}