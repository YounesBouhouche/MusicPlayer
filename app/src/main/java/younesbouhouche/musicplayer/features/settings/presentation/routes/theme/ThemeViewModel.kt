package younesbouhouche.musicplayer.features.settings.presentation.routes.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.domain.models.preferences.ColorScheme
import younesbouhouche.musicplayer.core.domain.models.preferences.Theme
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository

class ThemeViewModel(
    val repository: PreferencesRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val theme = repository.get(SettingsPreference.ThemeMode).onEach { theme ->
        _uiState.update {
            it.copy(selectedTheme = theme)
        }
    }.stateInVM(Theme.SYSTEM, viewModelScope)
    val dynamicColors = repository.get(SettingsPreference.DynamicColor)
        .stateInVM(false, viewModelScope)
    val colorScheme = repository.get(SettingsPreference.ColorSchemeMode)
        .stateInVM(ColorScheme.GREEN, viewModelScope)
    val extraDark = repository.get(SettingsPreference.ExtraDarkMode)
        .stateInVM(false, viewModelScope)

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
            theme?.let {
                repository.set(SettingsPreference.ThemeMode, it)
            }
            dynamic?.let {
                repository.set(SettingsPreference.DynamicColor, it)
            }
            color?.let {
                repository.set(SettingsPreference.ColorSchemeMode, it)
            }
            extraDark?.let {
                repository.set(SettingsPreference.ExtraDarkMode, it)
            }
        }
    }
}