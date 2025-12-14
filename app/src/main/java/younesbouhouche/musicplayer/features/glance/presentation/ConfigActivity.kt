package younesbouhouche.musicplayer.features.glance.presentation

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository
import younesbouhouche.musicplayer.features.settings.presentation.settingsLabel
import younesbouhouche.musicplayer.core.presentation.theme.AppTheme
import kotlin.math.roundToInt

class ConfigActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var glanceId: GlanceId
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        glanceId = GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
        setContent {
            val repository = koinInject<PreferencesRepository>()
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val opacity by repository.get(SettingsPreference.Opacity(appWidgetId))
                .collectAsState(1f)
            var selectedOpacity by remember { mutableFloatStateOf(1f) }
            val scope = rememberCoroutineScope()
            LaunchedEffect(opacity) {
                selectedOpacity = opacity
            }
            SetSystemBarColors()
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
                                IconButton(onClick = {
                                    val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                    setResult(RESULT_CANCELED, resultValue)
                                    finish()
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                }
                            },
                            scrollBehavior = scrollBehavior,
                        )
                    },
                    bottomBar = {
                        Row(Modifier.padding(16.dp).fillMaxWidth()) {
                            Button(
                                {
                                    scope.launch {
                                        repository.set(
                                            SettingsPreference.Opacity(appWidgetId),
                                            selectedOpacity
                                        )
                                        MyAppWidget().updateAll(this@ConfigActivity)
                                        val resultValue =
                                            Intent().putExtra(
                                                AppWidgetManager.EXTRA_APPWIDGET_ID,
                                                appWidgetId
                                            )
                                        setResult(RESULT_OK, resultValue)
                                        finish()
                                    }
                                },
                                Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.ok))
                            }
                        }
                    },
                    contentWindowInsets = WindowInsets(left = 16.dp, right = 16.dp),
                ) { paddingValues ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        settingsLabel({ stringResource(R.string.transparency) }) {
                            "${(selectedOpacity * 100).roundToInt()}%"
                        }
                        item {
                            Slider(
                                value = selectedOpacity,
                                onValueChange = { selectedOpacity = it },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                            )
                        }
                        settingsLabel {
                            stringResource(R.string.preview)
                        }
                        item {
                            //LargeWidgetContent(null, PlayerState(), {}, selectedOpacity)
                        }
                    }
                }
            }
        }
    }
}
