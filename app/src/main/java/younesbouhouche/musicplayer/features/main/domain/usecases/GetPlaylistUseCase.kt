package younesbouhouche.musicplayer.features.main.domain.usecases

import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class GetPlaylistUseCase(
    val repository: MusicRepository,
) {
    operator fun invoke(id: Long) = repository.getPlaylist(id)
}
