package younesbouhouche.musicplayer.settings.presentation

import android.app.LocaleManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.compose.KoinContext
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.settings.constants.SettingsMaps.languages
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.settings.presentation.util.findActivity
import younesbouhouche.musicplayer.ui.theme.AppTheme


class LanguageActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            KoinContext {
                val dataStore = get<SettingsDataStore>()
                val listState = rememberLazyListState()
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
                val context = LocalContext.current
                val language by dataStore.language.collectAsState(initial = "system")
                var selectedLanguage by remember { mutableStateOf("") }
                val scope = rememberCoroutineScope()
                SetSystemBarColors(dataStore)
                AppTheme {
                    Scaffold(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            LargeTopAppBar(
                                title = {
                                    Text(
                                        stringResource(id = R.string.language),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { finish() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                    }
                                },
                                scrollBehavior = scrollBehavior,
                            )
                        },
                    ) { paddingValues ->
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            state = listState,
                            contentPadding = paddingValues,
                        ) {
                            settingsRadioItems(
                                languages.toList(),
                                languages.map { it.key }.indexOf(language),
                                {
                                    selectedLanguage = languages.keys.elementAt(it)
                                    scope.launch {
                                        dataStore.saveSettings(language = languages.keys.elementAt(it))
                                        context.findActivity()?.runOnUiThread {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                context.getSystemService(LocaleManager::class.java)
                                                    .applicationLocales =
                                                    LocaleList.forLanguageTags(
                                                        if (selectedLanguage == "system") {
                                                            LocaleManagerCompat.getSystemLocales(context)[0]!!.language
                                                        } else {
                                                            selectedLanguage
                                                        },
                                                    )
                                            } else {
                                                AppCompatDelegate.setApplicationLocales(
                                                    LocaleListCompat.forLanguageTags(selectedLanguage),
                                                )
                                            }
                                        }
                                    }
                                },
                            ) { Text(stringResource(it.second)) }
                        }
                    }
                }
            }
        }
    }
}
