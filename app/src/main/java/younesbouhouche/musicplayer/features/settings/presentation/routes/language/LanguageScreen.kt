package younesbouhouche.musicplayer.features.settings.presentation.routes.language

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.preferences.Language
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.features.settings.presentation.util.Checked

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    modifier: Modifier = Modifier,
    onLanguageSelected: (Language) -> Unit,
) {
    val context = LocalContext.current
    val viewModel = koinViewModel<LanguageViewModel>()
    val language by viewModel.language.collectAsState()
    SettingsScreen(
        title = stringResource(R.string.language),
        icon = Icons.Default.Translate,
        modifier = modifier,
    ) {
        item {
            SettingsList(null) {
                Language.entries.forEachIndexed { index, lang ->
                    SettingsItem(
                        headline = stringResource(lang.label),
                        supporting = lang.getLocalizedName(context),
                        checked =
                            Checked(true, language == lang) {
                                viewModel.saveLanguage(lang)
                                onLanguageSelected(lang)
                            },
                        onClick = { onLanguageSelected(lang) },
                        shape = listItemShape(index, Language.entries.size),
                    )
                }
            }
        }
    }
}
