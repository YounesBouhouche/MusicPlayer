package younesbouhouche.musicplayer.navigation

import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import younesbouhouche.musicplayer.features.main.presentation.navigation.MainScreen
import younesbouhouche.musicplayer.features.permissions.presentation.PermissionsScreen
import younesbouhouche.musicplayer.features.settings.presentation.SettingsNavGraph
import younesbouhouche.musicplayer.navigation.routes.Graph

@Composable
fun AppNavGraph(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {
    val width = LocalView.current.width
    Surface(modifier.fillMaxSize()) {
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            transitionSpec = {
                val initialKey = initialState.key as? Graph
                val targetKey = targetState.key as? Graph
                val forward = (initialKey?.ordinal ?: 0) < (targetKey?.ordinal ?: 1)
                materialSharedAxisXIn(forward, width / 2) togetherWith
                        materialSharedAxisXOut(forward, width / 2)
            },
        ) { key ->
            when(key) {
                is Graph.Permissions -> {
                    NavEntry(key) {
                        PermissionsScreen()
                    }
                }
                is Graph.Main -> {
                    NavEntry(key) {
                        MainScreen {
                            backStack.add(Graph.Settings)
                        }
                    }
                }
                is Graph.Settings -> {
                    NavEntry(key) {
                        SettingsNavGraph()
                    }
                }
                else -> error("Unknown NavGraph key: $key")
            }
        }
    }
}