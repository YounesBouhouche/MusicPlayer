package younesbouhouche.musicplayer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.data.database.entities.QueueEntity
import younesbouhouche.musicplayer.core.data.database.entities.QueueSongCrossRef
import younesbouhouche.musicplayer.core.data.database.entities.QueueWithSongs

@Dao
interface QueueDao {
    @Transaction
    @Query("SELECT * from QueueEntity WHERE id = 0")
    fun observeQueue(): Flow<QueueWithSongs?>

    @Transaction
    @Query("SELECT * from QueueEntity WHERE id = 0")
    suspend fun getQueue(): QueueWithSongs?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItems(refs: List<QueueSongCrossRef>)

    @Query("DELETE FROM QUEUE_SONG_CROSS_REF")
    suspend fun clearQueue()

    @Query("DELETE FROM QueueEntity WHERE id = 0")
    suspend fun deleteQueue()

    @Query("INSERT INTO QueueEntity (id, currentIndex) VALUES (0, -1)")
    suspend fun createQueue()

    @Transaction
    suspend fun initQueue(items: List<Long>) {
        clearQueue()
        deleteQueue()
        createQueue()
        insertQueueItems(items.mapIndexed { index, item ->
            QueueSongCrossRef(
                songId = item,
                queueId = 0,
                position = index
            )
        })
    }

    @Upsert
    suspend fun upsertQueueItem(ref: QueueSongCrossRef)

    @Transaction
    suspend fun addItem(songId: Long, position: Int) {
        upsertQueueItem(
            QueueSongCrossRef(
                songId = songId,
                queueId = 0,
                position = position
            )
        )
    }

    @Query("DELETE FROM queue_song_cross_ref WHERE songId = :songId AND queueId = 0")
    suspend fun removeItem(songId: Long)

    @Query("DELETE FROM queue_song_cross_ref WHERE position = :position AND queueId = 0")
    suspend fun removeAt(position: Int)

    @Query("UPDATE queue_song_cross_ref SET position = :newPosition WHERE songId = :songId AND queueId = 0")
    suspend fun updatePosition(songId: Long, newPosition: Int)

    @Query("UPDATE QueueEntity SET currentIndex = :currentIndex WHERE id = 0")
    suspend fun updateCurrentIndex(currentIndex: Int)

    @Delete
    suspend fun deleteQueueItem(ref: QueueSongCrossRef)
}