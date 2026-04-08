package younesbouhouche.musicplayer.features.main.domain.usecases

import younesbouhouche.musicplayer.features.player.domain.repository.PlayerRepository

class GetPlayerStateUseCase(
    val repository: PlayerRepository,
) {
    operator fun invoke() = repository.getPlayerState()
}
