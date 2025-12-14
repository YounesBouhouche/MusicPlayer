package younesbouhouche.musicplayer.features.main.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetSongUseCase

class SongInfoViewModel(
    val mainViewModel: MainViewModel,
    getSongUseCase: GetSongUseCase,
    songId: Long
): ViewModel() {
    val song = getSongUseCase(songId).stateInVM(null, viewModelScope)

    fun play() {
        song.value?.let {
            mainViewModel.play(listOf(it.id))
        }
    }

    fun addToQueue() {

    }

    fun toggleFavorite() {

    }

    fun addToPlaylist() {

    }
}