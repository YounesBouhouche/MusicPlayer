package younesbouhouche.musicplayer.features.main.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import younesbouhouche.musicplayer.core.domain.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.features.main.domain.repo.PlaylistRepository

class FavoritesViewModel(
    mediaRepository: MediaRepository,
    playlistRepository: PlaylistRepository
): ViewModel() {
    val favorites = mediaRepository.getFavorites()
        .stateInVM(emptyList(), viewModelScope)
    val playlists = playlistRepository.getFavoritePlaylists()
        .stateInVM(emptyList(), viewModelScope)


}