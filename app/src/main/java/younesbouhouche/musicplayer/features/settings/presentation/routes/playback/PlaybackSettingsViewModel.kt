package younesbouhouche.musicplayer.features.settings.presentation.routes.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository

class PlaybackSettingsViewModel(
    private val repository: PreferencesRepository
): ViewModel() {
    val skipSilence = repository.get(SettingsPreference.SkipSilence)
        .stateInVM(false, viewModelScope)
    val rememberSpeed = repository.get(SettingsPreference.RememberSpeed)
        .stateInVM(false, viewModelScope)
    val rememberPitch = repository.get(SettingsPreference.RememberPitch)
        .stateInVM(false, viewModelScope)

    fun saveSettings(
        skipSilence: Boolean? = null,
        rememberSpeed: Boolean?  = null,
        rememberPitch: Boolean?  = null,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            skipSilence?.let {
                repository.set(SettingsPreference.SkipSilence, it)
            }
            rememberSpeed?.let {
                repository.set(SettingsPreference.RememberSpeed, it)
            }
            rememberPitch?.let {
                repository.set(SettingsPreference.RememberPitch, it)
            }
        }
    }
}