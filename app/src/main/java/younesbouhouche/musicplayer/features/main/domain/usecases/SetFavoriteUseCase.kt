package younesbouhouche.musicplayer.features.main.domain.usecases

import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class SetFavoriteUseCase(
    val repository: MusicRepository,
) {
    suspend operator fun invoke(
        songId: Long,
        isFavorite: Boolean,
    ) {
        repository.setFavoriteSong(songId, isFavorite)
    }
}
