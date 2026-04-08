package younesbouhouche.musicplayer.features.main.domain.usecases

import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetSongUseCase(
    val repository: MusicRepository,
) {
    suspend operator fun invoke(id: Long) = repository.getSong(id)
}
