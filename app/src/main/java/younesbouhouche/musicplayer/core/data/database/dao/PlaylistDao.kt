package younesbouhouche.musicplayer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistEntity
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistSongCrossRef
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistWithSongs
import younesbouhouche.musicplayer.core.data.database.entities.SongEntity

@Dao
interface PlaylistDao {
    @Transaction
    @Query("SELECT * FROM PlaylistEntity")
    fun getPlaylists(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Query("SELECT * FROM PlaylistEntity WHERE id=:id")
    suspend fun getPlaylist(id: Long): PlaylistWithSongs

    @Transaction
    @Query("""
        SELECT * FROM PlaylistEntity WHERE id=:id
    """)
    fun getPlaylistEntity(id: Long): Flow<PlaylistEntity>

    @Query("""
        SELECT SongEntity.* FROM SongEntity
        INNER JOIN playlist_song_cross_ref ON SongEntity.id = playlist_song_cross_ref.songId
        WHERE playlist_song_cross_ref.playlistId = :playlistId
        ORDER BY playlist_song_cross_ref.position ASC
    """)
    fun getPlaylistSongsOrdered(playlistId: Long): Flow<List<SongEntity>>

    fun observePlaylist(id: Long): Flow<PlaylistWithSongs> {
        val playlist = getPlaylistEntity(id)
        val songs = getPlaylistSongsOrdered(id)
        return combine(playlist, songs) { pl, sg ->
            PlaylistWithSongs(
                playlist = pl,
                songs = sg
            )
        }
    }

    @Upsert
    suspend fun upsertPlaylist(playlist: PlaylistEntity): Long

    @Query("UPDATE PlaylistEntity SET name = :newName WHERE id = :playlistId")
    suspend fun renamePlaylist(playlistId: Long, newName: String)

    @Query("UPDATE PlaylistEntity SET image = :image WHERE id = :playlistId")
    suspend fun updatePlaylistImage(playlistId: Long, image: String?)

    @Upsert
    suspend fun upsertPlaylistSong(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun deletePlaylistSong(playlistId: Long, songId: Long)

    @Query("DELETE FROM PlaylistEntity WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Transaction
    suspend fun setPlaylistSongs(playlistId: Long, songIds: List<Long>) {
        val playlist = observePlaylist(playlistId).first()
        val removedSongIds = playlist.songs.map { it.id } - songIds.toSet()
        for (songId in removedSongIds) {
            deletePlaylistSong(playlistId, songId)
        }
        songIds.mapIndexed { index, songId ->
            PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = songId,
                position = index
            )
        }.forEach {
            upsertPlaylistSong(it)
        }
    }
}