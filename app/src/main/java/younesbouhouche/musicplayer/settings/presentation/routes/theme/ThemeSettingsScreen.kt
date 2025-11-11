package younesbouhouche.musicplayer.settings.presentation.routes.theme

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.ButtonsRow
import younesbouhouche.musicplayer.core.presentation.util.ToggleButtonsRow
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.settings.domain.models.Theme
import younesbouhouche.musicplayer.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.settings.presentation.util.Category
import younesbouhouche.musicplayer.settings.presentation.util.Checked
import younesbouhouche.musicplayer.settings.presentation.util.SettingData
import younesbouhouche.musicplayer.settings.presentation.util.findActivity

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
                    bottomContent = {
                        Row(
                            Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            ToggleButtonsRow(
                                selected = { Theme.entries[it] == theme },
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
                                buttonContentPadding = PaddingValues(vertical = 12.dp)
                            ) {
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
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isCompatible)
                                item {
                                    ColorPreview(
                                        if (isDark) dynamicDarkColorScheme(context)
                                        else dynamicLightColorScheme(context),
                                        selected = dynamicColors
                                    ) {
                                        viewModel.saveSettings(dynamic = true, color = null)
                                    }
                                }
                            items(younesbouhouche.musicplayer.settings.domain.models.ColorScheme.entries) {
                                ColorPreview(
                                    it.scheme(isDark),
                                    selected = !dynamicColors and (colorTheme == it)
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



@Composable
internal fun ColorPreview(
    scheme: ColorScheme,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val color by animateColorAsState(
        if (selected) borderColor else Color.Transparent
    )
    Surface(
        modifier
            .size(100.dp)
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