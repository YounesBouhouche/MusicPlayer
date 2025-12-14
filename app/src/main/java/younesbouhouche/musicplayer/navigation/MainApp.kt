package younesbouhouche.musicplayer.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.permissions.presentation.Permissions
import younesbouhouche.musicplayer.navigation.routes.Graph

@Composable
fun MainApp(modifier: Modifier = Modifier) {
    val initialRoute =
        if (Permissions.AUDIO.isGranted(LocalContext.current)) Graph.Main
        else Graph.Permissions

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Graph.Permissions::class, Graph.Permissions.serializer())
                    subclass(Graph.Main::class, Graph.Main.serializer())
                    subclass(Graph.Settings::class, Graph.Settings.serializer())
                }
            }
        },
        initialRoute
    )
    EventHandler(
        { name, items ->
//            mainVM.onPlaylistEvent(PlaylistEvent.CreateNew(name, items, null))
        }
    ) {
        backStack.clear()
        backStack.add(Graph.Main)
    }
    AppNavGraph(
        backStack,
        modifier.fillMaxSize(),
    )
}