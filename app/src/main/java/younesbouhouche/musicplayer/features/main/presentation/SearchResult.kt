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
    fun isFilesEmpty(): Boolean = SearchFilter.FILES !in filters || files.isEmpty()

    fun isArtistsEmpty(): Boolean = SearchFilter.ARTISTS !in filters || artists.isEmpty()

    fun isAlbumsEmpty(): Boolean = SearchFilter.ALBUMS !in filters || albums.isEmpty()

    fun isPlaylistsEmpty(): Boolean = SearchFilter.PLAYLISTS !in filters || playlists.isEmpty()

    fun isEmpty(): Boolean = isFilesEmpty() && isArtistsEmpty() && isAlbumsEmpty() && isPlaylistsEmpty()

    fun isNotEmpty(): Boolean = !isEmpty()

    fun toggleFilter(filter: SearchFilter): SearchResult =
        if (filter in filters) {
            copy(filters = filters - filter)
        } else {
            copy(filters = filters + filter)
        }
}
