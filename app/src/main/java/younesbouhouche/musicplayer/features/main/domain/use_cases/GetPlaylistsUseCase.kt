package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetPlaylistsUseCase(val repository: MusicRepository) {
    operator fun invoke(): Flow<List<Playlist>> = repository.getPlaylists()
}