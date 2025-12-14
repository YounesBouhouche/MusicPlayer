package younesbouhouche.musicplayer.features.main.presentation.routes.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetSongsUseCase
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.presentation.util.SortType
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.main.util.sortBy

class LibraryViewModel(
    val mainViewModel: MainViewModel,
    getSongsUseCase: GetSongsUseCase
): ViewModel() {
    private val _sortState = MutableStateFlow(SortState(SortType.Title))
    val sortState = _sortState.asStateFlow()
    val songs = combine(getSongsUseCase(), _sortState) { songs, sortState ->
        songs.sortBy(sortState.sortType, sortState.ascending)
    }.stateInVM(emptyList(), viewModelScope)

    fun setSortState(sortState: SortState<SortType>) {
        _sortState.value = sortState
    }

    fun play(
        tracks: List<Long>,
        index: Int = 0,
        shuffle: Boolean = false
    ) = mainViewModel.play(tracks, index, shuffle)
}