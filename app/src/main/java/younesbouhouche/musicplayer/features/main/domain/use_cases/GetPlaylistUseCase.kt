package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetPlaylistUseCase(val repository: MusicRepository) {
    suspend operator fun invoke(id: Long): Playlist = repository.getPlaylist(id)
}