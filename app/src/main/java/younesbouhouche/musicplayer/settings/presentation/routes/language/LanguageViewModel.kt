package younesbouhouche.musicplayer.settings.presentation.routes.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.util.stateInVM
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.settings.domain.models.Language

class LanguageViewModel(
    val dataStore: SettingsDataStore
): ViewModel() {
    val language = dataStore.language.stateInVM(Language.SYSTEM, viewModelScope)

    fun saveLanguage(language: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.saveSettings(language = language.toString())
        }
    }
}