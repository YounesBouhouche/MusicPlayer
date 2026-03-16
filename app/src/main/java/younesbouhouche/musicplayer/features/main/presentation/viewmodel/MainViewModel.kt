package younesbouhouche.musicplayer.features.main.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetLoadingStateUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetPlayerStateUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.HandlePlayerEventUseCase
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEvent
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState

class MainViewModel(
    getPlayerStateUseCase: GetPlayerStateUseCase,
    val handlePlayerEventUseCase: HandlePlayerEventUseCase,
    getLoadingStateUseCase: GetLoadingStateUseCase
): ViewModel() {
    val playerState = getPlayerStateUseCase().stateInVM(PlayerState(), viewModelScope)
    val isLoading = getLoadingStateUseCase()

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