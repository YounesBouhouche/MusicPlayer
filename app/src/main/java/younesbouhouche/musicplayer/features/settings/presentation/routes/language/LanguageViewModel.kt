package younesbouhouche.musicplayer.features.settings.presentation.routes.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.core.data.datastore.PreferencesDataStore
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.domain.models.preferences.Language
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository

class LanguageViewModel(
    val preferencesRepository: PreferencesRepository
): ViewModel() {
    val language = preferencesRepository.get(SettingsPreference.LanguagePref)
        .stateInVM(Language.SYSTEM, viewModelScope)

    fun saveLanguage(language: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.set(SettingsPreference.LanguagePref, language)
        }
    }
}