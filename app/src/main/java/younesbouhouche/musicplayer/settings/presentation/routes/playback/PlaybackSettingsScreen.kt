package younesbouhouche.musicplayer.settings.presentation.routes.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.settings.presentation.util.Category
import younesbouhouche.musicplayer.settings.presentation.util.Checked
import younesbouhouche.musicplayer.settings.presentation.util.SettingData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSettingsScreen(
    viewModel: PlaybackSettingsViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    val skipSilence by viewModel.skipSilence.collectAsState()
    val rememberSpeed by viewModel.rememberSpeed.collectAsState()
    val rememberPitch by viewModel.rememberPitch.collectAsState()
    val settings = listOf(
        Category(
            name = R.string.playback,
            items = listOf(
                SettingData(
                    headline = R.string.skip_silence,
                    supporting = R.string.skip_silence_description,
                    icon = Icons.Default.MusicOff,
                    checked = Checked(false, skipSilence) {
                        viewModel.saveSettings(skipSilence = it)
                    },
                ) {
                    viewModel.saveSettings(skipSilence = !skipSilence)
                }
            ),
        ),
        Category(
            name = R.string.parameters,
            items = listOf(
                SettingData(
                    headline = R.string.keep_speed,
                    supporting = R.string.keep_speed_desc,
                    icon = Icons.Default.Speed,
                    checked = Checked(false, rememberSpeed) {
                        viewModel.saveSettings(rememberSpeed = it)
                    },
                ) {
                    viewModel.saveSettings(rememberSpeed = !rememberSpeed)
                },
                SettingData(
                    headline = R.string.keep_pitch,
                    supporting = R.string.keep_pitch_desc,
                    icon = Icons.Default.RecordVoiceOver,
                    checked = Checked(false, rememberPitch) {
                        viewModel.saveSettings(rememberPitch = it)
                    },
                ) {
                    viewModel.saveSettings(rememberPitch = !rememberPitch)
                },
            ),
        )
    )
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.playback),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
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
            contentPadding = paddingValues + PaddingValues(12.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(settings) {
                SettingsList(it.name) {
                    it.items.forEachIndexed { index, item ->
                        SettingsItem(
                            data = item,
                            shape = listItemShape(index, it.items.size),
                        )
                    }
                }
            }
        }
    }

}