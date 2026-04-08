package younesbouhouche.musicplayer.features.main.domain.usecases

import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class ObserveSongUseCase(
    val repository: MusicRepository,
) {
    operator fun invoke(id: Long) = repository.observeSong(id)
}
