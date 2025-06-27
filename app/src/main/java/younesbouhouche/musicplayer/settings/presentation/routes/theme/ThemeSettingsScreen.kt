package younesbouhouche.musicplayer.settings.presentation.routes.theme

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
import younesbouhouche.musicplayer.settings.presentation.util.findActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    isDark: Boolean,
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
) {
    val isCompatible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val theme by viewModel.theme.collectAsState()
    val colorTheme by viewModel.colorTheme.collectAsState()
    val extraDark by viewModel.extraDark.collectAsState()
    val dynamicColors by viewModel.dynamicColors.collectAsState()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val settings = listOf(
        Category(
            name = R.string.theme,
            items =  listOf(
                SettingData(
                    headline = R.string.app_theme,
                    supporting = theme.label,
                ) {
                    viewModel.update { copy(themeDialog = true) }
                },
                SettingData(
                    headline = R.string.black_theme,
                    supporting = R.string.extra_dark_description,
                    enabled = isDark,
                    checked = Checked(false, extraDark) {
                        viewModel.saveSettings(extraDark = it)
                    }
                ) {
                    viewModel.saveSettings(extraDark = !extraDark)
                }
            )
        ),
        Category(
            name = R.string.colors,
            items =  listOf(
                SettingData(
                    headline = R.string.dynamic_colors,
                    supporting = R.string.dynamic_theme_desc,
                    enabled = isCompatible,
                    checked = Checked(false, dynamicColors) {
                        viewModel.saveSettings(dynamic = it)
                    }
                ) {
                    viewModel.saveSettings(dynamic = !dynamicColors)
                },
                SettingData(
                    headline = R.string.color_palette,
                    supporting = R.string.color_palette_desc,
                    enabled = !dynamicColors,
                    trailingContent = {
                        ColorPreview(
                            if (isDark) colorTheme.darkScheme
                            else colorTheme.lightScheme,
                            selected = true,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                ) {
                    viewModel.update { copy(colorDialog = true) }
                },
            )
        )
    )
    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.theme_settings),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { context.findActivity()?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.navigationBars,
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