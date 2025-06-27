package younesbouhouche.musicplayer.settings.presentation.routes.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.util.stateInVM
import younesbouhouche.musicplayer.main.data.PlayerDataStore

class PlaybackSettingsViewModel(
    private val dataStore: PlayerDataStore
): ViewModel() {
    val skipSilence = dataStore.skipSilence.stateInVM(false, viewModelScope)
    val rememberSpeed = dataStore.rememberSpeed.stateInVM(false, viewModelScope)
    val rememberPitch = dataStore.rememberPitch.stateInVM(false, viewModelScope)

    fun saveSettings(
        skipSilence: Boolean? = null,
        rememberSpeed: Boolean?  = null,
        rememberPitch: Boolean?  = null,
        ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.saveSettings(
                skipSilence = skipSilence,
                rememberSpeed = rememberSpeed,
                rememberPitch = rememberPitch
            )
        }
    }
}