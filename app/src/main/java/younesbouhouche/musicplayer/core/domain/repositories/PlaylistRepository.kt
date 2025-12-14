package younesbouhouche.musicplayer.core.domain.repositories

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.models.Playlist

interface PlaylistRepository {
    suspend fun createPlaylist(name: String)
    suspend fun renamePlaylist(playlistId: Long, newName: String)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long, position: Int? = null)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    fun getPlaylists(): Flow<List<Playlist>>
}