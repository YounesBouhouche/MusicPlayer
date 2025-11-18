package younesbouhouche.musicplayer.features.settings.presentation.routes.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ToggleButtonsRow
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.presentation.util.plus
import younesbouhouche.musicplayer.features.settings.models.ColorScheme
import younesbouhouche.musicplayer.features.settings.models.Theme
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.features.settings.presentation.components.SettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.features.settings.presentation.util.Category
import younesbouhouche.musicplayer.features.settings.presentation.util.Checked
import younesbouhouche.musicplayer.features.settings.presentation.util.SettingData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ThemeSettingsScreen(
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<ThemeViewModel>()
    val isCompatible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val theme by viewModel.theme.collectAsState()
    val colorTheme by viewModel.colorTheme.collectAsState()
    val extraDark by viewModel.extraDark.collectAsState()
    val dynamicColors by viewModel.dynamicColors.collectAsState()
    val context = LocalContext.current
    val settings = listOf(
        Category(
            name = R.string.theme,
            items =  listOf(
                SettingData(
                    headline = R.string.app_theme,
                    bottomContent = {
                        Row(
                            Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            ToggleButtonsRow(
                                checked = { Theme.entries[it] == theme },
                                count = Theme.entries.size,
                                icon = {
                                    Theme.entries[it].icon
                                },
                                text = {
                                    stringResource(Theme.entries[it].label)
                                },
                                outlined = { true },
                                modifier = Modifier.padding(horizontal = 12.dp),
                                size = ButtonDefaults.ExtraSmallContainerHeight,
                                expandedWeight = 1.4f,
//                                buttonContentPadding = PaddingValues(vertical = 12.dp)
                            ) { it, _ ->
                                viewModel.saveSettings(theme = Theme.entries[it])
                            }
                        }
                    }
                ),
                SettingData(
                    headline = R.string.black_theme,
                    supporting = R.string.extra_dark_description,
                    enabled = isDark,
                    checked = Checked(false, extraDark) {
                        viewModel.saveSettings(extraDark = it)
                    }
                ) {
                    viewModel.saveSettings(extraDark = !extraDark)
                },
                SettingData(
                    headline = R.string.color_palette,
                    supporting = R.string.color_palette_desc,
                    bottomContent = {
                        LazyRow(
                            Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(
                                bottom = 16.dp,
                                start = 16.dp,
                                end = 16.dp,
                            ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isCompatible)
                                item {
                                    ColorPreview(
                                        if (isDark) dynamicDarkColorScheme(context)
                                        else dynamicLightColorScheme(context),
                                        selected = dynamicColors,
                                        size = 80.dp
                                    ) {
                                        viewModel.saveSettings(dynamic = true, color = null)
                                    }
                                }
                            items(ColorScheme.entries) {
                                ColorPreview(
                                    it.scheme(isDark),
                                    selected = !dynamicColors and (colorTheme == it),
                                    size = 80.dp
                                ) {
                                    viewModel.saveSettings(
                                        color = it, dynamic = false.takeIf { isCompatible }
                                    )
                                }
                            }
                        }
                    }
                )
            )
        ),
    )
    SettingsScreen(
        title = stringResource(R.string.theme_settings),
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



@Composable
internal fun ColorPreview(
    scheme: androidx.compose.material3.ColorScheme,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val color by animateColorAsState(
        if (selected) borderColor else Color.Transparent
    )
    Surface(
        modifier
            .size(size)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            2.dp,
            if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        ),
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .border(3.dp, color, CircleShape)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                ) {
                    repeat(2) { i ->
                        Row(
                            Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            repeat(2) { j ->
                                Surface(
                                    Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                    color = when (i * 2 + j) {
                                        0 -> scheme.primary
                                        1 -> scheme.secondary
                                        2 -> scheme.tertiary
                                        else -> scheme.surface
                                    }
                                ) {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}