package younesbouhouche.musicplayer.features.player.data.repository

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.features.player.domain.controller.PlayerController
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState
import younesbouhouche.musicplayer.features.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    val playerController: PlayerController
): PlayerRepository {
    override fun getPlayerState(): Flow<PlayerState> {
        return playerController.playerState
    }
}