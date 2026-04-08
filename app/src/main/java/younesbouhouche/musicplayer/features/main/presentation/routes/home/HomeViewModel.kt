package younesbouhouche.musicplayer.features.main.presentation.routes.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.usecases.GetHistoryUseCase
import younesbouhouche.musicplayer.features.main.domain.usecases.GetLastAddedUseCase
import younesbouhouche.musicplayer.features.main.domain.usecases.GetRecentAlbumsUseCase
import younesbouhouche.musicplayer.features.main.domain.usecases.GetRecentArtistsUseCase
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel

class HomeViewModel(
    val mainViewModel: MainViewModel,
    getRecentAlbumsUseCase: GetRecentAlbumsUseCase,
    getRecentArtistsUseCase: GetRecentArtistsUseCase,
    getHistoryUseCase: GetHistoryUseCase,
    getLastAddedUseCase: GetLastAddedUseCase,
) : ViewModel() {
    val artists = getRecentArtistsUseCase().stateInVM(emptyList(), viewModelScope)
    val albums = getRecentAlbumsUseCase().stateInVM(emptyList(), viewModelScope)
    val history = getHistoryUseCase().stateInVM(emptyList(), viewModelScope)
    val lastAdded = getLastAddedUseCase().stateInVM(emptyList(), viewModelScope)

    fun play(
        tracks: List<Long>,
        index: Int = 0,
        shuffle: Boolean = false,
    ) = mainViewModel.play(
        tracks = tracks,
        index = index,
        shuffle = shuffle,
    )
}
