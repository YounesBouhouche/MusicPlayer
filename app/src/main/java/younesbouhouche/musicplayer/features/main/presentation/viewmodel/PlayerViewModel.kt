package younesbouhouche.musicplayer.features.main.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.events.UiAction
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetPlayerStateUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetQueueUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetSongUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.HandlePlayerEventUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.SetFavoriteUseCase
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEvent
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEventBus
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState

class PlayerViewModel(
    private val handlePlayerEventUseCase: HandlePlayerEventUseCase,
    getPlayerStateUseCase: GetPlayerStateUseCase,
    getQueueUseCase: GetQueueUseCase,
    observeSongUseCase: GetSongUseCase,
    val setFavoriteUseCase: SetFavoriteUseCase
): ViewModel() {
    val playerState = getPlayerStateUseCase().stateInVM(PlayerState(), viewModelScope)

    private val _queue = getQueueUseCase()
    val queue = _queue.stateInVM(null, viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentItem = _queue.filter { it?.getCurrentItem() != null }.flatMapLatest {
        observeSongUseCase(it!!.getCurrentItem()!!.id)
    }.stateInVM(null, viewModelScope)

    init {
        viewModelScope.launch {
            async {
                PlayerEventBus.collectEvents {
                    handlePlayerEventUseCase(it)
                }
            }
            async {
                currentItem.collect {
                    println("CurrentItem updated: $it")
                }
            }
        }
    }

    fun onUiAction(action: UiAction) {

    }

    fun onPlayerEvent(event: PlayerEvent) {
        viewModelScope.launch {
            handlePlayerEventUseCase(event)
        }
    }

    fun setFavorite(songId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            println("Set favorite: $songId to $isFavorite")
            setFavoriteUseCase(songId, isFavorite)
        }
    }
}