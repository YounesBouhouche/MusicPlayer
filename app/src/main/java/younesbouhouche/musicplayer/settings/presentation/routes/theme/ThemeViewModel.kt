package younesbouhouche.musicplayer.settings.presentation.routes.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.util.stateInVM
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.settings.domain.models.ColorScheme
import younesbouhouche.musicplayer.settings.domain.models.Theme

class ThemeViewModel(val dataStore: SettingsDataStore): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.stateInVM(UiState(), viewModelScope)

    val theme = dataStore.theme.onEach { theme ->
        _uiState.update {
            it.copy(selectedTheme = theme)
        }
    }.stateInVM(Theme.SYSTEM, viewModelScope)
    val dynamicColors = dataStore.dynamicColors.stateInVM(false, viewModelScope)
    val colorTheme = dataStore.colorTheme.stateInVM(ColorScheme.GREEN, viewModelScope)
    val extraDark = dataStore.extraDark.stateInVM(false, viewModelScope)

    fun update(callback: UiState.() -> UiState) {
        _uiState.value = _uiState.value.callback()
    }

    fun saveSettings(
        theme: Theme? = null,
        dynamic: Boolean? = null,
        color: ColorScheme? = null,
        extraDark: Boolean? = null
    ) {
        viewModelScope.launch {
            dataStore.saveSettings(theme?.toString(), color?.toString(), dynamic, extraDark)
        }
    }
}