package younesbouhouche.musicplayer.core.domain.repositories

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.models.Playlist

interface PlaylistRepository {
    suspend fun createPlaylist(name: String, imageUri: Uri? = null)
    suspend fun setPlaylistSongs(playlistId: Long, songIds: List<Long>)
    suspend fun renamePlaylist(playlistId: Long, newName: String)
    suspend fun changePlaylistImage(playlistId: Long, imageUri: Uri?)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addSongsToPlaylist(playlistId: Long, ids: List<Long>, startPosition: Int? = null)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    fun getPlaylists(): Flow<List<Playlist>>
}