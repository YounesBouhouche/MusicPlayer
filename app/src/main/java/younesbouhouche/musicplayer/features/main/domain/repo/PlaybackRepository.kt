package younesbouhouche.musicplayer.features.main.domain.repo

import kotlinx.coroutines.flow.StateFlow
import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.features.main.presentation.states.PlayerState

interface PlaybackRepository {
    fun initialize()
    suspend fun onEvent(event: PlaybackEvent)
    suspend fun getPlayerState(): StateFlow<PlayerState>
}