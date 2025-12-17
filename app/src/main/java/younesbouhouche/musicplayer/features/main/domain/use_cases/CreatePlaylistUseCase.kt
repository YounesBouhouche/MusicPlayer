package younesbouhouche.musicplayer.features.main.domain.use_cases

import android.net.Uri
import younesbouhouche.musicplayer.core.domain.repositories.PlaylistRepository

class CreatePlaylistUseCase(val repository: PlaylistRepository) {
    suspend operator fun invoke(name: String, image: Uri?) = repository.createPlaylist(name, image)
}