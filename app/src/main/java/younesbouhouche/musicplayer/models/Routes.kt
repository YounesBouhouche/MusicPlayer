package younesbouhouche.musicplayer.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Routes(val title: String, val icon: ImageVector, val destination: NavRoutes, val index: Int) {
    Home("Home", Icons.Default.Home, NavRoutes.Home, 0),
    Albums("Albums", Icons.Default.Album, NavRoutes.Albums, 1),
    Artists("Artists", Icons.Default.Person, NavRoutes.Artists, 2),
    Playlists("Playlists", Icons.AutoMirrored.Default.PlaylistPlay, NavRoutes.Playlists, 3),
    Library("Library", Icons.Default.LibraryMusic, NavRoutes.Library, 4),
}

