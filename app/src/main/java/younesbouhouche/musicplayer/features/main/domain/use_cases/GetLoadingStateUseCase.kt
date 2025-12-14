package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetLoadingStateUseCase(val repository: MusicRepository) {
    operator fun invoke() = repository.getLoadingState()
}