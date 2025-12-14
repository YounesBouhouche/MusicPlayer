package younesbouhouche.musicplayer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.data.database.entities.PlayHistEntity
import younesbouhouche.musicplayer.core.data.database.entities.SongWithState

@Dao
interface PlayHistoryDao {
    @Transaction
    @Query("SELECT * FROM SongEntity INNER JOIN PlayHistEntity ON SongEntity.id = PlayHistEntity.songId GROUP BY SongEntity.id ORDER BY PlayHistEntity.playedAt DESC")
    fun getPlayHistory(): Flow<List<SongWithState>>

    @Query("SELECT * FROM PlayHistEntity WHERE songId = :songId ORDER BY playedAt DESC")
    suspend fun getSongPlayHistory(songId: Long): List<PlayHistEntity>

    @Upsert
    suspend fun addToPlayHistory(playHistEntity: PlayHistEntity)

    @Query("DELETE FROM PlayHistEntity")
    suspend fun clearPlayHistory()
}