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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
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
        setContent {
            SetSystemBarColors(settingsDataStore)
            val matchPictureColors by playerDataStore.matchPictureColors.collectAsState(initial = false)
            val showVolumeSlider by playerDataStore.showVolumeSlider.collectAsState(initial = false)
            val showRepeatButton by playerDataStore.showRepeat.collectAsState(initial = false)
            val showShuffleButton by playerDataStore.showShuffle.collectAsState(initial = false)
            val showSpeedButton by playerDataStore.showSpeed.collectAsState(initial = false)
            val showPitchButton by playerDataStore.showPitch.collectAsState(initial = false)
            val showTimerButton by playerDataStore.showTimer.collectAsState(initial = false)
            val showLyricsButton by playerDataStore.showLyrics.collectAsState(initial = false)
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
                        settingsLabel("Customize view")
                        checkSettingsItem(
                            Icons.Default.Colorize,
                            R.string.match_picture_colors,
                            R.string.match_picture_colors_description,
                            null,
                            matchPictureColors,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    matchPictureColors = it,
                                )
                            }
                        }
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
                        settingsLabel("Controls")
                        checkSettingsItem(
                            Icons.Default.Repeat,
                            R.string.show_repeat_button,
                            R.string.show_repeat_button,
                            null,
                            showRepeatButton,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    showRepeat = it,
                                )
                            }
                        }
                        checkSettingsItem(
                            Icons.Default.Shuffle,
                            R.string.show_shuffle_button,
                            R.string.show_shuffle_button,
                            null,
                            showShuffleButton,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    showShuffle = it,
                                )
                            }
                        }
                        checkSettingsItem(
                            Icons.Default.Speed,
                            R.string.show_speed_button,
                            R.string.show_speed_button,
                            null,
                            showSpeedButton,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    showSpeed = it,
                                )
                            }
                        }
                        checkSettingsItem(
                            Icons.Default.RecordVoiceOver,
                            R.string.show_pitch_button,
                            R.string.show_pitch_button,
                            null,
                            showPitchButton,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    showPitch = it,
                                )
                            }
                        }
                        checkSettingsItem(
                            Icons.Default.Timer,
                            R.string.show_timer_button,
                            R.string.show_timer_button,
                            null,
                            showTimerButton,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    showTimer = it,
                                )
                            }
                        }
                        checkSettingsItem(
                            Icons.Default.Lyrics,
                            R.string.show_lyrics_button,
                            R.string.show_lyrics_button,
                            null,
                            showLyricsButton,
                        ) {
                            scope.launch {
                                playerDataStore.saveSettings(
                                    showLyrics = it,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
