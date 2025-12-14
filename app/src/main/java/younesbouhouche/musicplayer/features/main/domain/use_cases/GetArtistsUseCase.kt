package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetArtistsUseCase(val repository: MusicRepository) {
    operator fun invoke(): Flow<List<Artist>> = repository.getArtists()
}