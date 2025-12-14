package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.features.player.domain.controller.PlayerController
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEvent

class HandlePlayerEventUseCase(val controller: PlayerController) {
    suspend operator fun invoke(event: PlayerEvent) {
        controller.handleEvent(event)
    }
}