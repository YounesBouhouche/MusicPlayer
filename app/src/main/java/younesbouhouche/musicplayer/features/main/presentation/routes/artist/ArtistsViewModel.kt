package younesbouhouche.musicplayer.features.main.presentation.routes.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetArtistsUseCase
import younesbouhouche.musicplayer.features.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.main.util.sortBy

class ArtistsViewModel(
    val mainViewModel: MainViewModel,
    getArtistsUseCase: GetArtistsUseCase,
): ViewModel() {

    private val _sortState = MutableStateFlow(SortState(ListsSortType.Name))
    val sortState = _sortState.asStateFlow()
    val artists = combine(getArtistsUseCase(), _sortState) { artists, sortState ->
        artists.sortBy(sortState.sortType, sortState.ascending)
    }.stateInVM(emptyList(), viewModelScope)

    fun play(
        tracks: List<Long>,
        index: Int = 0,
        shuffle: Boolean = false
    ) = mainViewModel.play(
        tracks = tracks,
        index = index,
        shuffle = shuffle
    )

    fun setSortState(state: SortState<ListsSortType>) {
        _sortState.value = state
    }
}