package younesbouhouche.musicplayer.features.player.domain.repository

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState

interface PlayerRepository {
    fun getPlayerState(): Flow<PlayerState>
}