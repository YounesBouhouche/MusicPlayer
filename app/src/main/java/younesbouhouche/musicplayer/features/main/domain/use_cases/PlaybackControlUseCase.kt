package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.features.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.features.main.domain.repo.PlaybackRepository

class PlaybackControlUseCase(val playbackRepository: PlaybackRepository) {
    suspend operator fun invoke(event: PlaybackEvent) {
        playbackRepository.onEvent(event)
    }
}