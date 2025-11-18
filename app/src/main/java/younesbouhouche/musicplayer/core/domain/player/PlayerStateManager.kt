package younesbouhouche.musicplayer.core.domain.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import younesbouhouche.musicplayer.features.main.presentation.states.PlayerState

class PlayerStateManager {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    fun updateState(update: (PlayerState) -> PlayerState) {
        _playerState.update(update)
    }

    suspend fun suspendUpdateState(update: suspend (PlayerState) -> PlayerState) {
        _playerState.value = update(_playerState.value)
    }
}