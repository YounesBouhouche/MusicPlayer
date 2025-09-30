package younesbouhouche.musicplayer.main.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.R

enum class Routes(
    val title: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val destination: NavRoutes,
    val index: Int
) {
    Home(
        R.string.home,
        Icons.Outlined.Home,
        Icons.Default.Home,
        NavRoutes.Home,
        0
    ),
    Albums(
        R.string.albums,
        Icons.Outlined.Album,
        Icons.Default.Album,
        NavRoutes.Albums,
        1
    ),
    Artists(
        R.string.artists,
        Icons.Outlined.Person,
        Icons.Default.Person,
        NavRoutes.Artists,
        2
    ),
    Playlists(
        R.string.playlists,
        Icons.AutoMirrored.Outlined.PlaylistPlay,
        Icons.AutoMirrored.Default.PlaylistPlay,
        NavRoutes.Playlists,
        3
    ),
    Library(
        R.string.library,
        Icons.Outlined.LibraryMusic,
        Icons.Default.LibraryMusic,
        NavRoutes.Library,
        4
    ),
}
