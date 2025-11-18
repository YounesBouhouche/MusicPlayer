package younesbouhouche.musicplayer.features.settings.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.get
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import younesbouhouche.musicplayer.features.main.presentation.util.setLanguage
import younesbouhouche.musicplayer.features.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.features.settings.presentation.routes.SettingsPage
import younesbouhouche.musicplayer.features.settings.presentation.routes.SettingsRoutes
import younesbouhouche.musicplayer.features.settings.presentation.routes.about.AboutScreen
import younesbouhouche.musicplayer.features.settings.presentation.routes.language.LanguageScreen
import younesbouhouche.musicplayer.features.settings.presentation.routes.playback.PlaybackSettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.routes.player.PlayerSettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.routes.theme.ThemeSettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.util.findActivity
import younesbouhouche.musicplayer.navigation.util.getRoute

@Composable
fun SettingsNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context.findActivity()!!
    val dataStore = activity.get<SettingsDataStore>()
    val isDark by dataStore.isDark().collectAsState(false)
    Surface(
        modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController,
            startDestination = SettingsRoutes.SettingsMain,
            enterTransition = {
                materialSharedAxisXIn(
                    initialState.getRoute(SettingsRoutes.allRoutes)
                            == SettingsRoutes.SettingsMain,
                    200
                )
            },
            exitTransition = {
                materialSharedAxisXOut(
                    initialState.getRoute(SettingsRoutes.allRoutes)
                            == SettingsRoutes.SettingsMain,
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
                LanguageScreen(Modifier.fillMaxSize(), activity::setLanguage)
            }
            composable<SettingsRoutes.AboutSettings> {
                AboutScreen(Modifier.fillMaxSize())
            }
        }
    }
}