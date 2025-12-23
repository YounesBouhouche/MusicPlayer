package younesbouhouche.musicplayer.features.settings.presentation.routes.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import com.younesb.mydesignsystem.presentation.util.plus
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.features.settings.presentation.util.Category
import younesbouhouche.musicplayer.features.settings.presentation.util.Checked
import younesbouhouche.musicplayer.features.settings.presentation.util.SettingData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSettingsScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<PlaybackSettingsViewModel>()
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
    SettingsScreen(
        title = stringResource(R.string.playback),
        modifier = modifier
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