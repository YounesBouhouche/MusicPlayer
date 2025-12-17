package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.repositories.PlaylistRepository

class SetPlaylistSongsUseCase(val repository: PlaylistRepository) {
    suspend operator fun invoke(playlistId: Long, songIds: List<Long>) {
        repository.setPlaylistSongs(playlistId, songIds)
    }
}