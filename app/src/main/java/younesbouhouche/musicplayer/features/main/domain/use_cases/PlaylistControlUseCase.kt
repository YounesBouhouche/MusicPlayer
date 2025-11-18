package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.features.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.features.main.domain.repo.PlaylistRepository

class PlaylistControlUseCase(val playlistRepository: PlaylistRepository) {
    suspend operator fun invoke(event: PlaylistEvent) {
        playlistRepository.onPlaylistEvent(event)
    }
}