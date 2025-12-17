package younesbouhouche.musicplayer.core.data.repositories

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import younesbouhouche.musicplayer.core.data.database.dao.PlaylistDao
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistEntity
import younesbouhouche.musicplayer.core.domain.mappers.toPlaylist
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.repositories.PlaylistRepository
import younesbouhouche.musicplayer.features.main.presentation.util.saveUriImageToInternalStorage

class PlaylistRepositoryImpl(
    val dao: PlaylistDao,
    val context: Context
): PlaylistRepository {
    override suspend fun createPlaylist(name: String, imageUri: Uri?) {
        val id = dao.upsertPlaylist(PlaylistEntity(name = name))
        changePlaylistImage(id, imageUri)
    }

    override suspend fun setPlaylistSongs(
        playlistId: Long,
        songIds: List<Long>
    ) {
        dao.setPlaylistSongs(playlistId, songIds)
    }

    override suspend fun renamePlaylist(playlistId: Long, newName: String) {
        dao.renamePlaylist(playlistId, newName)
    }

    override suspend fun changePlaylistImage(playlistId: Long, imageUri: Uri?) {
        val fileName = "pl_$playlistId.jpg"
        val saved = imageUri?.let {
            withContext(Dispatchers.IO) {
                saveUriImageToInternalStorage(context, it, fileName) != null
            }
        } == true
        dao.updatePlaylistImage(playlistId, fileName.takeIf { saved })
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        dao.deletePlaylist(playlistId)
    }

    override suspend fun addSongsToPlaylist(
        playlistId: Long,
        ids: List<Long>,
        startPosition: Int?
    ) {
        val list = dao.getPlaylist(playlistId).songs.map { it.id }
        val newItems = ids - list.toSet()
        val newList = if (startPosition != null) {
            val mutableList = list.toMutableList()
            mutableList.addAll(startPosition, newItems)
            mutableList.toList()
        } else {
            list + newItems
        }
        setPlaylistSongs(playlistId, newList)
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        dao.deletePlaylistSong(playlistId, songId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return dao.getPlaylists().map { flow -> flow.map { it.toPlaylist() } }
    }
}