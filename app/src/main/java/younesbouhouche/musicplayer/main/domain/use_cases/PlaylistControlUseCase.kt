package younesbouhouche.musicplayer.main.domain.use_cases

import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.repo.PlaylistRepository

class PlaylistControlUseCase(val playlistRepository: PlaylistRepository) {
    suspend operator fun invoke(event: PlaylistEvent) {
        playlistRepository.onPlaylistEvent(event)
    }
}