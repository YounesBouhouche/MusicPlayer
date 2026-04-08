package younesbouhouche.musicplayer.features.main.domain.usecases

import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetSongsUseCase(
    val repository: MusicRepository,
) {
    operator fun invoke() = repository.getSongsList()
}
