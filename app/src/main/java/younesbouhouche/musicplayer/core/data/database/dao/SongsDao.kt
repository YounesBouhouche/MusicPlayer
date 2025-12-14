package younesbouhouche.musicplayer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.data.database.entities.SongEntity
import younesbouhouche.musicplayer.core.data.database.entities.SongStateEntity
import younesbouhouche.musicplayer.core.data.database.entities.SongWithState

@Dao
interface SongsDao {
    @Transaction
    @Query("SELECT * from SongEntity")
    fun getSongs(): Flow<List<SongWithState>>

    @Query("SELECT * from SongEntity")
    suspend fun suspendGetSongs(): List<SongEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM SongEntity LIMIT 1)")
    suspend fun isLibraryInitialized(): Boolean

    @Upsert
    fun upsertSongs(songs: List<SongEntity>)

    @Transaction
    @Query("SELECT * from SongEntity WHERE id=:id")
    fun observeSongById(id: Long): Flow<SongWithState?>

    @Transaction
    @Query("SELECT * from SongEntity WHERE id IN (:ids)")
    fun observeSongsById(ids: List<Long>): Flow<List<SongWithState>>

    @Transaction
    @Query("SELECT * from SongEntity WHERE id=:id")
    fun getSongById(id: Long): SongWithState

    @Upsert
    suspend fun upsertSongStatus(songStateEntity: SongStateEntity)

    @Transaction
    suspend fun setSongFavoriteStatus(songId: Long, isFavorite: Boolean) =
        upsertSongStatus(
            SongStateEntity(
                songId = songId,
                isFavorite = isFavorite
            )
        )

    @Query("DELETE FROM SongEntity")
    suspend fun clearSongs()
}