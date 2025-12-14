package younesbouhouche.musicplayer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistEntity
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistSongCrossRef
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistWithSongs

@Dao
interface PlaylistDao {
    @Transaction
    @Query("SELECT * FROM PlaylistEntity")
    fun getPlaylists(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Query("SELECT * FROM PlaylistEntity WHERE id=:id")
    suspend fun getPlaylist(id: Long): PlaylistWithSongs

    @Upsert
    suspend fun upsertPlaylist(playlist: PlaylistEntity)

    @Query("UPDATE PlaylistEntity SET name = :newName WHERE id = :playlistId")
    suspend fun renamePlaylist(playlistId: Long, newName: String)

    @Upsert
    suspend fun upsertPlaylistSong(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun deletePlaylistSong(playlistId: Long, songId: Long)

    @Query("DELETE FROM PlaylistEntity WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)
}