package younesbouhouche.musicplayer.main.domain.use_cases

import younesbouhouche.musicplayer.main.domain.events.PlaybackEvent
import younesbouhouche.musicplayer.main.domain.repo.PlaybackRepository

class PlaybackControlUseCase(val playbackRepository: PlaybackRepository) {
    suspend operator fun invoke(event: PlaybackEvent) {
        playbackRepository.onEvent(event)
    }
}