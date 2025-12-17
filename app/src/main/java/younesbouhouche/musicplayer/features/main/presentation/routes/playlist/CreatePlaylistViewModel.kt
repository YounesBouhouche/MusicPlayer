package younesbouhouche.musicplayer.features.main.presentation.routes.playlist

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.features.main.domain.use_cases.CreatePlaylistUseCase

class CreatePlaylistViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase
): ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    private val _uri = MutableStateFlow<Uri?>(null)
    val uri = _uri.asStateFlow()

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onUriChange(newUri: Uri?) {
        _uri.value = newUri
    }

    fun createPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            createPlaylistUseCase(name.value, uri.value)
        }
    }
}