package younesbouhouche.musicplayer.features.settings.presentation.routes.language

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.presentation.util.plus
import younesbouhouche.musicplayer.features.settings.models.Language
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
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = paddingValues + PaddingValues(12.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsList(null) {
                    Language.entries.forEachIndexed { index, lang ->
                        SettingsItem(
                            headline = stringResource(lang.label),
                            supporting = lang.getLocalizedName(context),
                            checked = Checked(true, language == lang) {
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
}