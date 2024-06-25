package younesbouhouche.musicplayer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

enum class Routes(val title: String, val icon: ImageVector, val destination: NavRoutes, val index: Int) {
    Home("Home", Icons.Default.Home, NavRoutes.Home, 0),
    Albums("Albums", Icons.Default.Album, NavRoutes.Albums, 1),
    Artists("Artists", Icons.Default.Person, NavRoutes.Artists, 2),
    Playlists("Playlists", Icons.AutoMirrored.Default.PlaylistPlay, NavRoutes.Playlists, 3),
    Library("Library", Icons.Default.LibraryMusic, NavRoutes.Library, 4),
}

@Serializable
sealed class NavRoutes {
    @Serializable
    data object Home: NavRoutes()
    @Serializable
    data object Albums: NavRoutes()
    @Serializable
    data object Artists: NavRoutes()
    @Serializable
    data object Playlists: NavRoutes()
    @Serializable
    data object Library: NavRoutes()
    @Serializable
    data class ListScreen(val title: String): NavRoutes()
    @Serializable
    data class PlaylistScreen(val title: String): NavRoutes()
    @Serializable
    data object FavoritesScreen: NavRoutes()
    @Serializable
    data object MostPlayedScreen: NavRoutes()
    @Serializable
    data object RecentlyPlayedScreen: NavRoutes()
    @Serializable
    data object RecentlyAddedScreen: NavRoutes()
}
