package younesbouhouche.musicplayer.main.domain.use_cases

import younesbouhouche.musicplayer.main.domain.repo.MediaRepository

class SetFavoriteUseCase(val mediaRepository: MediaRepository) {
    suspend operator fun invoke(path: String, favorite: Boolean) {
        mediaRepository.setFavorite(path, favorite)
    }
}