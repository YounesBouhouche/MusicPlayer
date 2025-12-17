package younesbouhouche.musicplayer.core.domain.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.domain.models.LoadingState

interface MusicRepository {
    suspend fun refreshMusicLibrary(force: Boolean = false)

    fun getLoadingState(): StateFlow<LoadingState>

    suspend fun setFavoriteSong(songId: Long, isFavorite: Boolean)

    suspend fun addSongToPlayHistory(songId: Long)

    suspend fun clearPlayHistory()

    fun getSongsList(): Flow<List<Song>>

    fun getSong(id: Long): Flow<Song?>
    suspend fun getSongs(ids: List<Long>): List<Song>

    fun getRecentlyPlayedSongs(): Flow<List<Song>>

    fun getRecentArtists(): Flow<List<Artist>>

    fun getAlbums(): Flow<List<Album>>

    fun getArtists(): Flow<List<Artist>>

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun getAlbum(name: String): Album

    suspend fun getArtist(name: String): Artist

    fun getPlaylist(id: Long): Flow<Playlist>
}