package younesbouhouche.musicplayer.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.data.database.dao.PlaylistDao
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistEntity
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistSongCrossRef
import younesbouhouche.musicplayer.core.domain.mappers.toPlaylist
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.repositories.PlaylistRepository

class PlaylistRepositoryImpl(
    val dao: PlaylistDao
): PlaylistRepository {
    override suspend fun createPlaylist(name: String) {
        dao.upsertPlaylist(PlaylistEntity(name = name))
    }

    override suspend fun renamePlaylist(playlistId: Long, newName: String) {
        dao.renamePlaylist(playlistId, newName)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        dao.deletePlaylist(playlistId)
    }

    override suspend fun addSongToPlaylist(
        playlistId: Long,
        songId: Long,
        position: Int?
    ) {
        dao.upsertPlaylistSong(PlaylistSongCrossRef(playlistId, songId, position ?: 0))
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        dao.deletePlaylistSong(playlistId, songId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return dao.getPlaylists().map { flow -> flow.map { it.toPlaylist() } }
    }
}