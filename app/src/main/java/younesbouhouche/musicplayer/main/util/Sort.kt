package younesbouhouche.musicplayer.main.util

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.main.presentation.util.SortType

fun List<MusicCard>.sortBy(sortType: SortType, ascending: Boolean = true) =
    if (ascending) {
        when (sortType) {
            SortType.Title -> sortedBy { it.title }
            SortType.Filename -> sortedBy { it.path }
            SortType.Duration -> sortedBy { it.duration }
            SortType.Date -> sortedBy { it.date }
            SortType.Size -> sortedBy { it.size }
        }
    } else {
        when (sortType) {
            SortType.Title -> sortedByDescending { it.title }
            SortType.Filename -> sortedByDescending { it.path }
            SortType.Duration -> sortedByDescending { it.duration }
            SortType.Date -> sortedByDescending { it.date }
            SortType.Size -> sortedByDescending { it.size }
        }
    }

fun List<MusicCard>.sortBy(sortType: PlaylistSortType, ascending: Boolean = true) =
    if (ascending) {
        when (sortType) {
            PlaylistSortType.Custom -> this
            PlaylistSortType.Title -> sortedBy { it.title }
            PlaylistSortType.Filename -> sortedBy { it.path }
            PlaylistSortType.Duration -> sortedBy { it.duration }
            PlaylistSortType.Date -> sortedBy { it.date }
            PlaylistSortType.Size -> sortedBy { it.size }
        }
    } else {
        when (sortType) {
            PlaylistSortType.Custom -> reversed()
            PlaylistSortType.Title -> sortedByDescending { it.title }
            PlaylistSortType.Filename -> sortedByDescending { it.path }
            PlaylistSortType.Duration -> sortedByDescending { it.duration }
            PlaylistSortType.Date -> sortedByDescending { it.date }
            PlaylistSortType.Size -> sortedByDescending { it.size }
        }
    }

@JvmName("sortByAlbum")
fun List<Album>.sortBy(sortType: ListsSortType, ascending: Boolean = true) =
    if (ascending) {
        when (sortType) {
            ListsSortType.Name -> sortedBy { it.name }
            ListsSortType.Count -> sortedBy { it.items.size }
        }
    } else {
        when (sortType) {
            ListsSortType.Name -> sortedByDescending { it.name }
            ListsSortType.Count -> sortedByDescending { it.items.size }
        }
    }


@JvmName("sortByArtist")
fun List<Artist>.sortBy(sortType: ListsSortType, ascending: Boolean = true) =
    if (ascending) {
        when (sortType) {
            ListsSortType.Name -> sortedBy { it.name }
            ListsSortType.Count -> sortedBy { it.items.size }
        }
    } else {
        when (sortType) {
            ListsSortType.Name -> sortedByDescending { it.name }
            ListsSortType.Count -> sortedByDescending { it.items.size }
        }
    }

@JvmName("sortByPlaylist")
fun List<Playlist>.sortBy(sortType: ListsSortType, ascending: Boolean = true) =
    if (ascending) {
        when (sortType) {
            ListsSortType.Name -> sortedBy { it.name }
            ListsSortType.Count -> sortedBy { it.items.size }
        }
    } else {
        when (sortType) {
            ListsSortType.Name -> sortedByDescending { it.name }
            ListsSortType.Count -> sortedByDescending { it.items.size }
        }
    }
