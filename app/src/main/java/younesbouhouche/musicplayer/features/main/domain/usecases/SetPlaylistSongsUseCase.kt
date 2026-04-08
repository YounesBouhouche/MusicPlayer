package younesbouhouche.musicplayer.features.main.domain.usecases

import younesbouhouche.musicplayer.core.domain.repositories.PlaylistRepository

class SetPlaylistSongsUseCase(
    val repository: PlaylistRepository,
) {
    suspend operator fun invoke(
        playlistId: Long,
        songIds: List<Long>,
    ) {
        repository.setPlaylistSongs(playlistId, songIds)
    }
}
