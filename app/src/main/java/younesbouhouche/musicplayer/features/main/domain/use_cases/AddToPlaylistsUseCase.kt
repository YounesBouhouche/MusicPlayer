package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.repositories.PlaylistRepository

class AddToPlaylistsUseCase(val repository: PlaylistRepository) {
    suspend operator fun invoke(ids: List<Long>, playlists: List<Long>) {
        for (playlistId in playlists) {
            repository.addSongsToPlaylist(playlistId, ids)
        }
    }
}