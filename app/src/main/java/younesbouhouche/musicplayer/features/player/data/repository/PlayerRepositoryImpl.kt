package younesbouhouche.musicplayer.features.player.data.repository

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.features.player.domain.controller.PlayerController
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState
import younesbouhouche.musicplayer.features.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    val playerStateManager: PlayerStateManager
): PlayerRepository {
    override fun getPlayerState(): Flow<PlayerState> {
        return playerStateManager.playerState
    }
}