package younesbouhouche.musicplayer.main.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Playlist

enum class ListsSortType(
    val label: Int,
    val icon: ImageVector,
    val sortAlbums: (List<Album>) -> List<Album>,
    val sortArtists: (List<Artist>) -> List<Artist>,
    val sortPlaylists: (List<Playlist>) -> List<Playlist>,
) {
    Name(
        R.string.title,
        Icons.Default.Title,
        { it.sortedBy { (name, _, _) -> name } },
        { it.sortedBy { (_, items, _, _) -> items.size } },
        { it.sortedBy { (_, name, _, _, _) -> name } }
    ),
    Count(
        R.string.items_count,
        Icons.AutoMirrored.Default.List,
        { it.sortedBy { (name, _, _) -> name } },
        { it.sortedBy { (_, items, _, _) -> items.size } },
        { it.sortedBy { (_, _, _, items, _) -> items.size } }
    ),
}