package younesbouhouche.musicplayer.features.main.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.models.LoadingState
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetLoadingStateUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetPlayerStateUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetQueueUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.HandlePlayerEventUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.ScanLibraryUseCase
import younesbouhouche.musicplayer.features.main.presentation.states.UiState
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEvent
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEventBus
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState

class MainViewModel(
    val scanLibraryUseCase: ScanLibraryUseCase,
    getPlayerStateUseCase: GetPlayerStateUseCase,
    val handlePlayerEventUseCase: HandlePlayerEventUseCase,
    getLoadingStateUseCase: GetLoadingStateUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    val playerState = getPlayerStateUseCase().stateInVM(PlayerState(), viewModelScope)

    val loadingState = getLoadingStateUseCase()

    init {
        scanLibrary()
    }

    fun scanLibrary(force: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            scanLibraryUseCase(force)
        }
    }

    fun play(
        tracks: List<Long>,
        index: Int = 0,
        shuffle: Boolean = false
    ) {
        viewModelScope.launch {
            handlePlayerEventUseCase(PlayerEvent.Play(
                tracks = tracks,
                index = index,
                shuffle = shuffle
            ))
        }
    }
}