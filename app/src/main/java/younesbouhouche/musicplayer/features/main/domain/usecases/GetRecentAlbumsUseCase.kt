package younesbouhouche.musicplayer.features.main.domain.usecases

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetRecentAlbumsUseCase(
    val repository: MusicRepository,
) {
    operator fun invoke(): Flow<List<Album>> = repository.getRecentAlbums()
}
