package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository

class RefreshUseCase(val mediaRepository: MediaRepository) {
    suspend operator fun invoke(onFinished: suspend () -> Unit = {}, callback: suspend () -> Unit = {}) {
        mediaRepository.refreshMediaLibrary(callback)
        onFinished()
    }
}