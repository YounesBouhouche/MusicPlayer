package younesbouhouche.musicplayer.settings.presentation.routes.language

import android.app.LocaleManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.settings.domain.models.Language
import younesbouhouche.musicplayer.ui.theme.AppTheme

class LanguageActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val viewModel = koinViewModel<LanguageViewModel>()
            val language by viewModel.language.collectAsState()
            AppTheme {
                LanguageScreen(
                    language = language,
                    onBack = { finish() }
                ) { selectedLanguage ->
                    viewModel.saveLanguage(selectedLanguage)
                    runOnUiThread {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            getSystemService(LocaleManager::class.java)
                                .applicationLocales =
                                LocaleList.forLanguageTags(
                                    if (selectedLanguage == Language.SYSTEM) {
                                        LocaleManagerCompat.getSystemLocales(this)[0]!!.language
                                    } else {
                                        selectedLanguage.tag
                                    },
                                )
                        } else {
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(selectedLanguage.tag),
                            )
                        }
                    }
                }
            }
        }
    }
}

