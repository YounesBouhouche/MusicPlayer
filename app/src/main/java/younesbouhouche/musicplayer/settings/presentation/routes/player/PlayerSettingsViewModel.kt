package younesbouhouche.musicplayer.settings.presentation.routes.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.util.stateInVM
import younesbouhouche.musicplayer.main.data.PlayerDataStore

class PlayerSettingsViewModel(val dataStore: PlayerDataStore): ViewModel() {
    val matchPictureColors = dataStore.matchPictureColors.stateInVM(false, viewModelScope)
    val showVolumeSlider = dataStore.showVolumeSlider.stateInVM(false, viewModelScope)
    val showRepeatButton = dataStore.showRepeat.stateInVM(false, viewModelScope)
    val showShuffleButton = dataStore.showShuffle.stateInVM(false, viewModelScope)
    val showSpeedButton = dataStore.showSpeed.stateInVM(false, viewModelScope)
    val showPitchButton = dataStore.showPitch.stateInVM(false, viewModelScope)
    val showTimerButton = dataStore.showTimer.stateInVM(false, viewModelScope)
    val showLyricsButton = dataStore.showLyrics.stateInVM(false, viewModelScope)

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
            dataStore.saveSettings(
                matchPictureColors,
                rememberRepeat,
                rememberShuffle,
                rememberSpeed,
                rememberPitch,
                showVolumeSlider,
                showRepeatButton,
                showShuffleButton,
                showSpeedButton,
                showPitchButton,
                showTimerButton,
                showLyricsButton,
                skipSilence,
            )
        }
    }
}