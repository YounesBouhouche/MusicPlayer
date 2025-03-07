package younesbouhouche.musicplayer.settings.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicOff
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.ui.theme.AppTheme
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackActivity : ComponentActivity() {
    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @Inject
    lateinit var playerDataStore: PlayerDataStore

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetSystemBarColors(settingsDataStore)
            val skipSilence by playerDataStore.skipSilence.collectAsState(initial = false)
            val scope = rememberCoroutineScope()
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val listState = rememberLazyListState()
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
                                    stringResource(id = R.string.player),
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
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                ) { paddingValues ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = listState,
                        contentPadding = paddingValues,
                    ) {
                        checkSettingsItem(
                            Icons.Default.MusicOff,
                            R.string.skip_silence,
                            R.string.skip_silence_description,
                            null,
                            skipSilence,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    skipSilence = it,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
