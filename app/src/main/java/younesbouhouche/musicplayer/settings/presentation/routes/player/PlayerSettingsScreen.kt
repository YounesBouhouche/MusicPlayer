package younesbouhouche.musicplayer.settings.presentation.routes.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.core.presentation.util.TopBar
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.settings.presentation.components.SettingsScreen
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.settings.presentation.util.Category
import younesbouhouche.musicplayer.settings.presentation.util.Checked
import younesbouhouche.musicplayer.settings.presentation.util.SettingData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettingsScreen(modifier: Modifier = Modifier) {
    val settings = listOf(
        Category(
            items = listOf(
                SettingData(
                    headline = R.string.style,
                    large = true
                ) {
                },
            ),
        ),
        Category(
            items = listOf(
                SettingData(
                    headline = R.string.carousel,
                    large = true
                ) {
                },
                SettingData(
                    headline = R.string.action_bar,
                    large = true
                ) {
                },
            ),
        ),
    )
    SettingsScreen(
        title = stringResource(R.string.player),
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
