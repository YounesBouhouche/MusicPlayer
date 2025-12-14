package younesbouhouche.musicplayer.features.settings.presentation.routes.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository
import younesbouhouche.musicplayer.core.presentation.util.stateInVM

class PlayerSettingsViewModel(val repository: PreferencesRepository): ViewModel() {
    val matchPictureColors = repository.get(SettingsPreference.MatchPictureColors)
        .stateInVM(false, viewModelScope)
    val showVolumeSlider = repository.get(SettingsPreference.ShowVolumeSlider)
        .stateInVM(false, viewModelScope)
    val showRepeatButton = repository.get(SettingsPreference.ShowRepeat)
        .stateInVM(false, viewModelScope)
    val showShuffleButton = repository.get(SettingsPreference.ShowShuffle)
        .stateInVM(false, viewModelScope)
    val showSpeedButton = repository.get(SettingsPreference.ShowSpeed)
        .stateInVM(false, viewModelScope)
    val showPitchButton = repository.get(SettingsPreference.ShowPitch)
        .stateInVM(false, viewModelScope)
    val showTimerButton = repository.get(SettingsPreference.ShowTimer)
        .stateInVM(false, viewModelScope)
    val showLyricsButton = repository.get(SettingsPreference.ShowLyrics)
        .stateInVM(false, viewModelScope)

    fun saveSettings(
        matchPictureColors: Boolean? = null,
        rememberRepeat: Boolean? = null,
        rememberShuffle: Boolean? = null,
        rememberSpeed: Boolean? = null,
        rememberPitch: Boolean? = null,
        showVolumeSlider: Boolean? = null,
        showRepeatButton: Boolean? = null,
        showShuffleButton: Boolean? = null,
        showSpeedButton: Boolean? = null,
        showPitchButton: Boolean? = null,
        showTimerButton: Boolean? = null,
        showLyricsButton: Boolean? = null,
        skipSilence: Boolean? = null,
    ) {
        viewModelScope.launch {
            matchPictureColors?.let {
                repository.set(SettingsPreference.MatchPictureColors, it)
            }
            rememberRepeat?.let {
                repository.set(SettingsPreference.RememberRepeat, it)
            }
            rememberShuffle?.let {
                repository.set(SettingsPreference.RememberShuffle, it)
            }
            rememberSpeed?.let {
                repository.set(SettingsPreference.RememberSpeed, it)
            }
            rememberPitch?.let {
                repository.set(SettingsPreference.RememberPitch, it)
            }
            showVolumeSlider?.let {
                repository.set(SettingsPreference.ShowVolumeSlider, it)
            }
            showRepeatButton?.let {
                repository.set(SettingsPreference.ShowRepeat, it)
            }
            showShuffleButton?.let {
                repository.set(SettingsPreference.ShowShuffle, it)
            }
            showSpeedButton?.let {
                repository.set(SettingsPreference.ShowSpeed, it)
            }
            showPitchButton?.let {
                repository.set(SettingsPreference.ShowPitch, it)
            }
            showTimerButton?.let {
                repository.set(SettingsPreference.ShowTimer, it)
            }
            showLyricsButton?.let {
                repository.set(SettingsPreference.ShowLyrics, it)
            }
            skipSilence?.let {
                repository.set(SettingsPreference.SkipSilence, it)
            }
        }
    }
}