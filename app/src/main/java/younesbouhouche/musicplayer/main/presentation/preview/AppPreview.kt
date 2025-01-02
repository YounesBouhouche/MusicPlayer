package younesbouhouche.musicplayer.main.presentation.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.glance.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.data.db.AppDatabase
import younesbouhouche.musicplayer.main.presentation.NavBar
import younesbouhouche.musicplayer.main.presentation.NavigationScreen
import younesbouhouche.musicplayer.main.presentation.SearchScreen
import younesbouhouche.musicplayer.main.presentation.states.SearchState
import younesbouhouche.musicplayer.main.presentation.viewmodel.MainVM

@Preview
@Composable
fun AppPreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    Box(Modifier.fillMaxSize()) {
        NavigationScreen(
            context,
            navController,
            MainVM(
                context,
                PlayerDataStore(context),
                db,
            ),
        )
        SearchScreen(
            SearchState(),
            false,
            {},
            {},
            {},
        )
        NavBar(
            visible = true,
            progress = 0f,
            state = 0,
        ) {
            navController.navigate(it) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
