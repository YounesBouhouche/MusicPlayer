package younesbouhouche.musicplayer.features.main.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.R

enum class SearchFilter(val label: Int, val icon: ImageVector) {
    FILES(label = R.string.files, icon = Icons.Default.MusicNote),
    ARTISTS(label = R.string.artists, icon = Icons.Default.Person),
    ALBUMS(label = R.string.albums, icon = Icons.Default.Album),
    PLAYLISTS(label = R.string.playlists, icon = Icons.AutoMirrored.Filled.PlaylistPlay),
//    FOLDERS(label = R.string.folders, icon = Icons.Default.Folder),
//    GENRES(label = R.string.genres, icon = Icons.Default.Category)
}