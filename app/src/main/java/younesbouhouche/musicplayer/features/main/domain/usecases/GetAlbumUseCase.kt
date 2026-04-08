package younesbouhouche.musicplayer.features.main.domain.usecases

import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetAlbumUseCase(
    val repository: MusicRepository,
) {
    suspend operator fun invoke(name: String): Album = repository.getAlbum(name)
}
