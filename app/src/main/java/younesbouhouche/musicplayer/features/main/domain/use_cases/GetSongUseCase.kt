package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetSongUseCase(val repository: MusicRepository) {
    operator fun invoke(id: Long) = repository.getSong(id)
}