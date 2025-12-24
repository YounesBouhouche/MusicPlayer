package younesbouhouche.musicplayer.core.domain.repositories

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.models.Queue

interface QueueRepository {
    fun observeQueue(): Flow<Queue?>
    suspend fun getQueue(): Queue?
    suspend fun createQueue(queue: List<Long>)
    suspend fun setCurrentIndex(index: Int)
    suspend fun clearQueue()
    suspend fun updatePosition(songId: Long, position: Int)

    suspend fun swapPositions(from: Int, to: Int) {
        println("Swapping positions from $from to $to")
        val queue = getQueue() ?: return
        val songFrom = queue.songs.getOrNull(from)?.id ?: return
        val songTo = queue.songs.getOrNull(to)?.id ?: return
        updatePosition(songFrom, to)
        updatePosition(songTo, from)
        println("New queue: ${getQueue()?.songs?.map { "${it.title}\n" }}")
    }

    suspend fun remove(songId: Long)
    suspend fun removeAt(index: Int)
    suspend fun add(songId: Long, position: Int)
}