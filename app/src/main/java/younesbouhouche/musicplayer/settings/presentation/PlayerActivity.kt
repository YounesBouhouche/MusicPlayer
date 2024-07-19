package younesbouhouche.musicplayer.settings.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speed
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
class PlayerActivity : ComponentActivity() {
    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @Inject
    lateinit var playerDataStore: PlayerDataStore

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SetSystemBarColors(settingsDataStore)
            val showVolumeSlider by playerDataStore.showVolumeSlider.collectAsState(initial = false)
            val showPitch by playerDataStore.showPitch.collectAsState(initial = false)
            val repeatMode by playerDataStore.rememberRepeat.collectAsState(initial = true)
            val shuffle by playerDataStore.rememberShuffle.collectAsState(initial = false)
            val speed by playerDataStore.rememberSpeed.collectAsState(initial = false)
            val pitch by playerDataStore.rememberPitch.collectAsState(initial = false)
            val scope = rememberCoroutineScope()
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val listState = rememberLazyListState()
            AppTheme {
                Scaffold(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        Column {
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
                        }
                    },
                ) { paddingValues ->
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(paddingValues),
                        state = listState,
                    ) {
                        settingsLabel("Customize view")
                        checkSettingsItem(
                            Icons.AutoMirrored.Default.VolumeUp,
                            R.string.show_volume_slider,
                            R.string.show_volume_slider,
                            null,
                            showVolumeSlider,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    showVolumeSlider = it,
                                )
                            }
                        }
                        checkSettingsItem(
                            Icons.Default.RecordVoiceOver,
                            R.string.show_pitch_button,
                            R.string.show_pitch_button,
                            null,
                            showPitch,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    showPitch = it,
                                )
                            }
                        }
                        settingsLabel("Remember options")
                        checkSettingsItem(
                            Icons.Default.Repeat,
                            R.string.remember_repeat_mode,
                            R.string.remember_repeat_mode,
                            null,
                            repeatMode,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    rememberRepeat = it,
                                )
                            }
                        }
                        checkSettingsItem(
                            Icons.Default.Shuffle,
                            R.string.remember_shuffle,
                            R.string.remember_shuffle,
                            null,
                            shuffle,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    rememberShuffle = it,
                                )
                            }
                        }
                        checkSettingsItem(
                            Icons.Default.Speed,
                            R.string.remember_playback_speed,
                            R.string.remember_playback_speed,
                            null,
                            speed,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    rememberSpeed = it,
                                )
                            }
                        }
                        if (showPitch) {
                            checkSettingsItem(
                                Icons.Default.RecordVoiceOver,
                                R.string.remember_pitch,
                                R.string.remember_pitch,
                                null,
                                pitch,
                            ) {
                                scope.launch {
                                    playerDataStore.saveSettings(
                                        rememberPitch = it,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
