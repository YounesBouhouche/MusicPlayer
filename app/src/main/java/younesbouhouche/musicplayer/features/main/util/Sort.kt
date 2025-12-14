package younesbouhouche.musicplayer.features.main.util

import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.features.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.features.main.presentation.util.SortType

fun List<Song>.sortBy(sortType: SortType, ascending: Boolean = true) =
    if (ascending) sortType.sort(this)
    else sortType.sort(this).reversed()

fun List<Song>.sortBy(sortType: PlaylistSortType, ascending: Boolean = true) =
    if (ascending) sortType.sort(this)
    else sortType.sort(this).reversed()

@JvmName("sortByAlbum")
fun List<Album>.sortBy(sortType: ListsSortType, ascending: Boolean = true) =
    if (ascending) sortType.sortAlbums(this)
    else sortType.sortAlbums(this).reversed()


@JvmName("sortByArtist")
fun List<Artist>.sortBy(sortType: ListsSortType, ascending: Boolean = true) =
    if (ascending) sortType.sortArtists(this)
    else sortType.sortArtists(this).reversed()

@JvmName("sortByPlaylist")
fun List<Playlist>.sortBy(sortType: ListsSortType, ascending: Boolean = true) =
    if (ascending) sortType.sortPlaylists(this)
    else sortType.sortPlaylists(this).reversed()