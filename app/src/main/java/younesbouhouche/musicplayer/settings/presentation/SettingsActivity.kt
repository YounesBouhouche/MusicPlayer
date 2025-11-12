package younesbouhouche.musicplayer.settings.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.get
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import younesbouhouche.musicplayer.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.main.presentation.util.setLanguage
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.settings.presentation.routes.SettingsPage
import younesbouhouche.musicplayer.settings.presentation.routes.SettingsRoutes
import younesbouhouche.musicplayer.settings.presentation.routes.about.AboutScreen
import younesbouhouche.musicplayer.settings.presentation.routes.language.LanguageScreen
import younesbouhouche.musicplayer.settings.presentation.routes.playback.PlaybackSettingsScreen
import younesbouhouche.musicplayer.settings.presentation.routes.player.PlayerSettingsScreen
import younesbouhouche.musicplayer.settings.presentation.routes.theme.ThemeSettingsScreen
import younesbouhouche.musicplayer.ui.theme.AppTheme


class SettingsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val dataStore = get<SettingsDataStore>()
            SetSystemBarColors(dataStore = dataStore)
            val isDark by dataStore.isDark().collectAsState(false)
            AppTheme {
                Surface(
                    Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController,
                        startDestination = SettingsRoutes.SettingsMain,
                        enterTransition = {
                            materialSharedAxisXIn(
                                initialState.destination.route?.let {
                                        SettingsRoutes.SettingsMain
                                            .javaClass
                                            .kotlin
                                            .qualifiedName
                                            ?.contains(it)
                                        } ?: false,
                                        200
                            )
                        },
                        exitTransition = {
                            materialSharedAxisXOut(
                                initialState.destination.route?.let {
                                    SettingsRoutes.SettingsMain
                                        .javaClass
                                        .kotlin
                                        .qualifiedName
                                        ?.contains(it)
                                } ?: false,
                                200
                            )
                        }
                    ) {
                        composable<SettingsRoutes.SettingsMain> {
                            SettingsPage(Modifier.fillMaxSize()) {
                                navController.navigate(it)
                            }
                        }
                        composable<SettingsRoutes.ThemeSettings> {
                            ThemeSettingsScreen(isDark, Modifier.fillMaxSize())
                        }
                        composable<SettingsRoutes.PlayerSettings> {
                            PlayerSettingsScreen(Modifier.fillMaxSize())
                        }
                        composable<SettingsRoutes.PlaybackSettings> {
                            PlaybackSettingsScreen(Modifier.fillMaxSize())
                        }
                        composable<SettingsRoutes.LanguageSettings> {
                            LanguageScreen(Modifier.fillMaxSize(), ::setLanguage)
                        }
                        composable<SettingsRoutes.AboutSettings> {
                            AboutScreen(Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}
