package younesbouhouche.musicplayer.features.main.presentation.navigation

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

enum class TopLevelRoutes(
    val title: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val destination: MainNavRoute,
    val index: Int
) {
    Home(
        R.string.home,
        Icons.Outlined.Home,
        Icons.Default.Home,
        MainNavRoute.Home,
        0
    ),
    Albums(
        R.string.albums,
        Icons.Outlined.Album,
        Icons.Default.Album,
        MainNavRoute.Albums,
        1
    ),
    Artists(
        R.string.artists,
        Icons.Outlined.Person,
        Icons.Default.Person,
        MainNavRoute.Artists,
        2
    ),
    Playlists(
        R.string.playlists,
        Icons.AutoMirrored.Outlined.PlaylistPlay,
        Icons.AutoMirrored.Default.PlaylistPlay,
        MainNavRoute.Playlists,
        3
    ),
    Library(
        R.string.library,
        Icons.Outlined.LibraryMusic,
        Icons.Default.LibraryMusic,
        MainNavRoute.Library,
        4
    ),
}
