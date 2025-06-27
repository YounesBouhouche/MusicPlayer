package younesbouhouche.musicplayer.main.domain.repo

import kotlinx.coroutines.flow.StateFlow
import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.presentation.states.PlayerState

interface PlaybackRepository {
    fun initialize()
    suspend fun onEvent(event: PlaybackEvent)
    suspend fun getPlayerState(): StateFlow<PlayerState>
}