package younesbouhouche.musicplayer.features.main.presentation.routes.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetAlbumUseCase
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.presentation.util.SortType
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.main.util.sortBy

class AlbumViewModel(
    val mainViewModel: MainViewModel,
    getAlbumUseCase: GetAlbumUseCase,
    albumName: String
): ViewModel() {
    private val _sortState = MutableStateFlow(SortState(SortType.Title))
    val sortState = _sortState.asStateFlow()
    private val _album = MutableStateFlow(Album(name = albumName))
    val album = combine(_album, _sortState) { album, sortState ->
        album.copy(
            songs = album.songs.sortBy(sortState.sortType, sortState.ascending)
        )
    }.stateInVM(Album(name = albumName), viewModelScope)

    init {
        viewModelScope.launch {
            _album.value = getAlbumUseCase(albumName)
        }
    }

    fun play(
        tracks: List<Long>,
        index: Int = 0,
        shuffle: Boolean = false
    ) = mainViewModel.play(tracks, index, shuffle)

    fun setSortState(sortState: SortState<SortType>) {
        _sortState.value = sortState
    }
}