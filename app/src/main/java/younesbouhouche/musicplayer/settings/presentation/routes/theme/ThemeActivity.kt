package younesbouhouche.musicplayer.settings.presentation.routes.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import org.koin.android.ext.android.get
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.presentation.components.Dialog
import younesbouhouche.musicplayer.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.settings.domain.models.ColorScheme
import younesbouhouche.musicplayer.settings.domain.models.Theme
import younesbouhouche.musicplayer.ui.theme.AppTheme

class ThemeActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val dataStore = get<SettingsDataStore>()
            SetSystemBarColors(dataStore = dataStore)
            val isDark by dataStore.isDark().collectAsState(false)
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val theme by themeViewModel.theme.collectAsState()
            val colorTheme by themeViewModel.colorTheme.collectAsState()
            val uiState by themeViewModel.uiState.collectAsState()
            AppTheme {
                ThemeSettingsScreen(isDark, themeViewModel)
                Dialog(
                    visible = uiState.themeDialog,
                    onDismissRequest = { themeViewModel.update {
                        copy(themeDialog = false)
                    } },
                    title = stringResource(R.string.theme),
                    centerTitle = true,
                    cancelListener = {
                        themeViewModel.update {
                            copy(themeDialog = false, selectedTheme = theme)
                        }
                    },
                    okListener = {
                        themeViewModel.saveSettings(theme = uiState.selectedTheme)
                        themeViewModel.update {
                            copy(themeDialog = false)
                        }
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Theme.entries.forEach {
                            Column(
                                Modifier.Companion.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Surface(
                                    Modifier.Companion
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(MaterialTheme.shapes.large)
                                        .clickable {
                                            themeViewModel.update {
                                                copy(selectedTheme = it)
                                            }
                                        },
                                    shape = MaterialTheme.shapes.large,
                                    border = BorderStroke(
                                        2.dp,
                                        if (uiState.selectedTheme == it)
                                            MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline
                                    ),
                                ) {
                                    Box(
                                        Modifier.Companion.fillMaxSize(),
                                        contentAlignment = Alignment.Companion.Center
                                    ) {
                                        Icon(
                                            it.icon,
                                            null,
                                            modifier = Modifier.Companion.fillMaxSize(.5f),
                                            tint =
                                                if (uiState.selectedTheme == it)
                                                    MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                Text(
                                    stringResource(it.label),
                                    modifier = Modifier.Companion.fillMaxWidth(),
                                    textAlign = TextAlign.Companion.Center,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Companion.Ellipsis,
                                )
                            }
                        }
                    }
                }
                Dialog(
                    visible = uiState.colorDialog,
                    onDismissRequest = {
                        themeViewModel.update {
                            copy(colorDialog = false)
                        }
                                       },
                    title = stringResource(R.string.color_palette),
                    centerTitle = true,
                    cancelListener = {
                        themeViewModel.update {
                            copy(colorDialog = false, selectedColor = colorTheme)
                        }
                    },
                    okListener = {
                        themeViewModel.saveSettings(color = uiState.selectedColor)
                        themeViewModel.update {
                            copy(colorDialog = false)
                        }
                                 },
                ) {
                    LazyRow(
                        Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(ColorScheme.entries) {
                            Surface(
                                Modifier
                                    .size(100.dp)
                                    .clip(MaterialTheme.shapes.large)
                                    .clickable {
                                        themeViewModel.update {
                                            copy(selectedColor = it)
                                        }
                                    },
                                shape = MaterialTheme.shapes.large,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                border = BorderStroke(
                                    2.dp,
                                    if (uiState.selectedColor == it)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                                ),
                            ) {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Companion.Center
                                ) {
                                    ColorPreview(
                                        scheme = if (isDark) it.darkScheme
                                            else it.lightScheme,
                                        modifier = Modifier.fillMaxSize(.6f),
                                        selected = uiState.selectedColor == it,
                                        borderColor = MaterialTheme.colorScheme.onSurface
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

@Composable
fun ColorPreview(
    scheme: androidx.compose.material3.ColorScheme,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    selected: Boolean = false,
) {
    val color by animateColorAsState(
        if (selected) borderColor else Color.Transparent
    )
    Box(
        modifier = modifier.border(3.dp, color, CircleShape).padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.fillMaxSize().clip(CircleShape)) {
            repeat(2) { i ->
                Row(Modifier.fillMaxSize().weight(1f)) {
                    repeat(2) { j ->
                        Surface(
                            Modifier.fillMaxSize().weight(1f),
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