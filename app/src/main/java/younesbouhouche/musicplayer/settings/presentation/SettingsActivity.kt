package younesbouhouche.musicplayer.settings.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import org.koin.android.ext.android.get
import org.koin.compose.KoinContext
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.settings.presentation.util.Settings
import younesbouhouche.musicplayer.ui.theme.AppTheme


class SettingsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            SetSystemBarColors(get())
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
                                    stringResource(id = R.string.settings),
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
                    contentWindowInsets = WindowInsets.navigationBars,
                ) { paddingValues ->
                    val categories = Settings.categories
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = listState,
                        contentPadding = paddingValues + PaddingValues(12.dp, 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        categories.forEach { category ->
                            item {
                                SettingsList(
                                    name = category.name,
                                    itemsCount = category.items.size
                                ) {
                                    SettingsItem(
                                        category.items[it],
                                        iconTint = category.iconTint,
                                        iconBackground = category.iconBackground,
                                        shape = listItemShape(it, category.items.size),
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
