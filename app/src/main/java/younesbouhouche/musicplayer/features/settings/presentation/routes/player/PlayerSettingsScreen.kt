package younesbouhouche.musicplayer.features.settings.presentation.routes.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import com.younesb.mydesignsystem.presentation.util.plus
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.features.settings.presentation.util.Category
import younesbouhouche.musicplayer.features.settings.presentation.util.SettingData

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
