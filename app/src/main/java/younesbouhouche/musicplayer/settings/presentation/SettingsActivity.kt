package younesbouhouche.musicplayer.settings.presentation

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.twotone.Brush
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Language
import androidx.compose.material.icons.twotone.LibraryMusic
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.view.WindowCompat
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.ui.theme.AppTheme

class SettingsActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val languages = mapOf(
                "system" to R.string.follow_system,
                "en" to R.string.english,
                "fr" to R.string.french,
                "ar" to R.string.arabic,
                "es" to R.string.spanish,
                "it" to R.string.italian,
                "in" to R.string.hindi
            )
            val dataStore = SettingsDataStore(LocalContext.current)
            val language by dataStore.language.collectAsState(initial = "system")
            val isDark = when (SettingsDataStore(context).theme.collectAsState(initial = "system").value) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            DisposableEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = if (!isDark) {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    },
                    navigationBarStyle = if(!isDark){
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    }
                )
                onDispose { }
            }
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val listState = rememberLazyListState()
            AppTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        topBar = {
                            Column {
                                LargeTopAppBar(
                                    title = {
                                        Text(
                                            stringResource(id = R.string.settings),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = { (context as Activity).finish() }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                        }
                                    },
                                    scrollBehavior = scrollBehavior
                                )
                            }
                        }
                    ) { paddingValues ->
                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingValues), state = listState) {
                            largeSettingsItem(
                                Icons.TwoTone.Brush,
                                R.string.theme,
                                R.string.customize_app_look,
                                onClick = {
                                    startActivity(Intent(context, ThemeActivity::class.java))
                                }
                            )
                            largeSettingsItem(
                                Icons.TwoTone.Language,
                                R.string.language,
                                languages[language]!!,
                                onClick = {
                                    startActivity(Intent(context, LanguageActivity::class.java))
                                }
                            )
                            largeSettingsItem(
                                Icons.TwoTone.PlayArrow,
                                R.string.player,
                                R.string.customize_player,
                                onClick = {
                                }
                            )
                            largeSettingsItem(
                                Icons.TwoTone.LibraryMusic,
                                R.string.library,
                                R.string.library,
                                onClick = {
                                }
                            )
                            largeSettingsItem(
                                Icons.TwoTone.Info,
                                R.string.about,
                                R.string.about_description,
                                onClick = {
                                    startActivity(Intent(context, AboutActivity::class.java))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}