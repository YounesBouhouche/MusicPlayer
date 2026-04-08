package younesbouhouche.musicplayer.features.main.domain.usecases

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetLastAddedUseCase(
    val repository: MusicRepository,
) {
    operator fun invoke(): Flow<List<Song>> = repository.getLastAddedSongs()
}
