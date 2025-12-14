package younesbouhouche.musicplayer.features.main.presentation

import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.Song

data class SearchResult(
    val filters: Set<SearchFilter> = emptySet(),
    val files: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
) {
    fun isFilesEmpty(): Boolean {
        return SearchFilter.FILES !in filters || files.isEmpty()
    }
    fun isArtistsEmpty(): Boolean {
        return SearchFilter.ARTISTS !in filters || artists.isEmpty()
    }
    fun isAlbumsEmpty(): Boolean {
        return SearchFilter.ALBUMS !in filters || albums.isEmpty()
    }
    fun isPlaylistsEmpty(): Boolean {
        return SearchFilter.PLAYLISTS !in filters || playlists.isEmpty()
    }
    fun isEmpty(): Boolean {
        return isFilesEmpty() && isArtistsEmpty() && isAlbumsEmpty() && isPlaylistsEmpty()
    }
    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }
    fun toggleFilter(filter: SearchFilter): SearchResult {
        return if (filter in filters) {
            copy(filters = filters - filter)
        } else {
            copy(filters = filters + filter)
        }
    }
}