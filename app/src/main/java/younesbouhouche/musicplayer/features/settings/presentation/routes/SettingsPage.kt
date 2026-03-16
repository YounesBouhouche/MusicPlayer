package younesbouhouche.musicplayer.features.settings.presentation.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import younesbouhouche.musicplayer.features.settings.presentation.util.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    navigate: (SettingsGraph) -> Unit,
) {
    val categories = Settings.categories
    SettingsScreen(
        title = stringResource(R.string.settings),
        icon = Icons.Default.Settings,
        modifier = modifier.fillMaxSize()
    ) {
        items(categories) { category ->
            SettingsList(
                name = category.name,
                itemsCount = category.items.size
            ) {
                SettingsItem(
                    headline = stringResource(category.items[it].headline),
                    supporting = category.items[it].supporting?.let { resId ->
                        stringResource(resId)
                    },
                    icon = category.items[it].icon,
                    iconTint = category.iconTint,
                    iconBackground = category.iconBackground,
                    background = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = listItemShape(it, category.items.size),
                    onClick = {
                        category.items[it].navigateToRoute?.let { route ->
                            navigate(route)
                        }
                    }
                )
            }
        }
    }
}