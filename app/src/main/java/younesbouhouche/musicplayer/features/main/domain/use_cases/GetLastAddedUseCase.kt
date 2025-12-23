package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetLastAddedUseCase(val repository: MusicRepository) {
    operator fun invoke(): Flow<List<Song>> = repository.getLastAddedSongs()
}