package younesbouhouche.musicplayer.settings.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.twotone.Audiotrack
import androidx.compose.material.icons.twotone.Category
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Language
import androidx.compose.material.icons.twotone.Palette
import androidx.compose.material.icons.twotone.PlayArrow
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.compose.KoinContext
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.settings.constants.SettingsMaps.languages
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.ui.theme.AppTheme


class SettingsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            KoinContext {
                SetSystemBarColors(get())
                val context = LocalContext.current
                val dataStore by inject<SettingsDataStore>()
                val language by dataStore.language.collectAsState(initial = "system")
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
                val listState = rememberLazyListState()
                val themeSettings = listOf(
                    LargeSettingsItem(
                        R.string.customize_app,
                        R.string.customize_app,
                        Icons.TwoTone.Palette
                    ) {
                        context.startActivity(Intent(context, ThemeActivity::class.java))
                    },
                    LargeSettingsItem(
                        R.string.customize_player,
                        R.string.customize_player,
                        Icons.TwoTone.PlayArrow
                    ) {
                        context.startActivity(Intent(context, PlayerActivity::class.java))
                    }
                )
                val playbackSettings = listOf(
                    LargeSettingsItem(
                        R.string.library,
                        R.string.library,
                        Icons.TwoTone.Category
                    ) {
                    },
                    LargeSettingsItem(
                        R.string.playback,
                        R.string.playback,
                        Icons.TwoTone.Audiotrack
                    ) {
                        context.startActivity(Intent(context, PlaybackActivity::class.java))
                    }
                )
                val globalSettings = listOf(
                    LargeSettingsItem(
                        R.string.language,
                        languages.getOrDefault(language, R.string.english),
                        Icons.TwoTone.Language
                    ) {
                        context.startActivity(Intent(context, LanguageActivity::class.java))
                    },
                    LargeSettingsItem(
                        R.string.about,
                        R.string.about_description,
                        Icons.TwoTone.Info
                    ) {
                        context.startActivity(Intent(context, AboutActivity::class.java))
                    }
                )
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
                                        stringResource(id = R.string.settings),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { (context as Activity).finish() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                    }
                                },
                                scrollBehavior = scrollBehavior,
                            )
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    ) { paddingValues ->
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            state = listState,
                            contentPadding = paddingValues,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                LargeSettingsGroup(themeSettings)
                            }
                            item {
                                LargeSettingsGroup(playbackSettings)
                            }
                            item {
                                LargeSettingsGroup(globalSettings)
                            }
                        }
                    }
                }
            }
        }
    }
}
