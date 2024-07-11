package younesbouhouche.musicplayer.main.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.R

enum class Routes(val title: Int, val icon: ImageVector, val destination: NavRoutes, val index: Int) {
    Home(R.string.home, Icons.Default.Home, NavRoutes.Home, 0),
    Albums(R.string.albums, Icons.Default.Album, NavRoutes.Albums, 1),
    Artists(R.string.artists, Icons.Default.Person, NavRoutes.Artists, 2),
    Playlists(R.string.playlists, Icons.AutoMirrored.Default.PlaylistPlay, NavRoutes.Playlists, 3),
    Library(R.string.library, Icons.Default.LibraryMusic, NavRoutes.Library, 4),
}
