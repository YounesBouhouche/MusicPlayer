package younesbouhouche.musicplayer.features.main.presentation.routes.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.AddToPlaylistsUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.CreatePlaylistUseCase
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetPlaylistsUseCase

class AddToPlaylistViewModel(
    private val addToPlaylistUseCase: AddToPlaylistsUseCase,
    getPlaylistsUseCase: GetPlaylistsUseCase
): ViewModel() {
    private val _selected = MutableStateFlow(emptySet<Long>())
    val selected = _selected.asStateFlow()

    val playlists = getPlaylistsUseCase().stateInVM(emptyList(), viewModelScope)

    fun onClearSelection() {
        _selected.value = emptySet()
    }

    fun onToggleSelection(id: Long) {
        _selected.value = _selected.value.toMutableSet().apply {
            if (contains(id)) remove(id)
            else add(id)
        }
    }

    fun addToPlaylists(ids: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            addToPlaylistUseCase(ids, _selected.value.toList())
        }
    }
}