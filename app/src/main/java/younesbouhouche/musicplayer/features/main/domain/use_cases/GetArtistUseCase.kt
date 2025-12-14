package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetArtistUseCase(val repository: MusicRepository) {
    suspend operator fun invoke(name: String): Artist = repository.getArtist(name)
}