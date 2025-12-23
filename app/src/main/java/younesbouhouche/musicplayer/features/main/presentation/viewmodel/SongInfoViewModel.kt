package younesbouhouche.musicplayer.features.main.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.ObserveSongUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.SetFavoriteUseCase

class SongInfoViewModel(
    val mainViewModel: MainViewModel,
    val setFavoriteUseCase: SetFavoriteUseCase,
    getSongUseCase: ObserveSongUseCase,
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
        song.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                setFavoriteUseCase(it.id, !it.isFavorite)
            }
        }
    }
}