package younesbouhouche.musicplayer.features.main.presentation.routes.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetHistoryUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetRecentArtistsUseCase
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel

class HomeViewModel(
    val mainViewModel: MainViewModel,
    getRecentArtistsUseCase: GetRecentArtistsUseCase,
    getHistoryUseCase: GetHistoryUseCase
): ViewModel() {
    val artists = getRecentArtistsUseCase().stateInVM(emptyList(), viewModelScope)
    val history = getHistoryUseCase().stateInVM(emptyList(), viewModelScope)

    fun play(
        tracks: List<Long>,
        index: Int = 0,
        shuffle: Boolean = false
    ) = mainViewModel.play(
        tracks = tracks,
        index = index,
        shuffle = shuffle
    )
}