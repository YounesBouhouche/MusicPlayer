package younesbouhouche.musicplayer.features.settings.presentation

import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import younesbouhouche.musicplayer.features.main.presentation.util.setLanguage
import younesbouhouche.musicplayer.features.settings.presentation.routes.SettingsGraph
import younesbouhouche.musicplayer.features.settings.presentation.routes.SettingsPage
import younesbouhouche.musicplayer.features.settings.presentation.routes.about.AboutScreen
import younesbouhouche.musicplayer.features.settings.presentation.routes.language.LanguageScreen
import younesbouhouche.musicplayer.features.settings.presentation.routes.playback.PlaybackSettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.routes.player.PlayerSettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.routes.theme.ThemeSettingsScreen
import younesbouhouche.musicplayer.features.settings.presentation.util.findActivity

@Composable
fun SettingsNavGraph(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(configuration = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(
                    SettingsGraph.SettingsMain::class,
                    SettingsGraph.SettingsMain.serializer()
                )
                subclass(
                    SettingsGraph.ThemeSettings::class,
                    SettingsGraph.ThemeSettings.serializer()
                )
                subclass(
                    SettingsGraph.PlayerSettings::class,
                    SettingsGraph.PlayerSettings.serializer()
                )
                subclass(
                    SettingsGraph.PlaybackSettings::class,
                    SettingsGraph.PlaybackSettings.serializer()
                )
                subclass(
                    SettingsGraph.LanguageSettings::class,
                    SettingsGraph.LanguageSettings.serializer()
                )
                subclass(
                    SettingsGraph.AboutSettings::class,
                    SettingsGraph.AboutSettings.serializer()
                )
            }
        }
    }, SettingsGraph.SettingsMain)
    val context = LocalContext.current
    val activity = context.findActivity()!!
    val width = LocalView.current.width
    Surface(
        modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavDisplay(
            backStack = backStack,
            transitionSpec = {
                val initialKey = initialState.key as? SettingsGraph
                val targetKey = targetState.key as? SettingsGraph
                val forward = (initialKey?.ordinal ?: 0) < (targetKey?.ordinal ?: 1)
                materialSharedAxisXIn(forward, width / 2) togetherWith
                        materialSharedAxisXOut(forward, width / 2)
            },
            popTransitionSpec = {
                val initialKey = initialState.key as? SettingsGraph
                val targetKey = targetState.key as? SettingsGraph
                val forward = (initialKey?.ordinal ?: 0) > (targetKey?.ordinal ?: 1)
                materialSharedAxisXIn(forward, width / 2) togetherWith
                        materialSharedAxisXOut(forward, width / 2)
            },
            predictivePopTransitionSpec = {
                val initialKey = initialState.key as? SettingsGraph
                val targetKey = targetState.key as? SettingsGraph
                val forward = (initialKey?.ordinal ?: 0) > (targetKey?.ordinal ?: 1)
                materialSharedAxisXIn(forward, width / 2) togetherWith
                        materialSharedAxisXOut(forward, width / 2)
            },
        ) { key ->
            when(key) {
                is SettingsGraph.SettingsMain -> {
                    NavEntry(key) {
                        SettingsPage(Modifier.fillMaxSize()) {
                            backStack.add(it)
                        }
                    }
                }
                is SettingsGraph.ThemeSettings -> {
                    NavEntry(key) {
                        ThemeSettingsScreen(Modifier.fillMaxSize())
                    }
                }
                is SettingsGraph.PlayerSettings -> {
                    NavEntry(key) {
                        PlayerSettingsScreen(Modifier.fillMaxSize())
                    }
                }
                is SettingsGraph.PlaybackSettings -> {
                    NavEntry(key) {
                        PlaybackSettingsScreen(Modifier.fillMaxSize())
                    }
                }
                is SettingsGraph.LanguageSettings -> {
                    NavEntry(key) {
                        LanguageScreen(Modifier.fillMaxSize(), activity::setLanguage)
                    }
                }
                is SettingsGraph.AboutSettings -> {
                    NavEntry(key) {
                        AboutScreen(Modifier.fillMaxSize())
                    }
                }
                else -> error("Unknown Settings Graph: $key")
            }
        }
    }
}