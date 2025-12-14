package younesbouhouche.musicplayer.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.data.database.dao.QueueDao
import younesbouhouche.musicplayer.core.data.database.entities.QueueSongCrossRef
import younesbouhouche.musicplayer.core.domain.mappers.toQueue
import younesbouhouche.musicplayer.core.domain.models.Queue
import younesbouhouche.musicplayer.core.domain.repositories.QueueRepository

class QueueRepositoryImpl(
    val dao: QueueDao
): QueueRepository {
    override fun observeQueue(): Flow<Queue?> {
        return dao.observeQueue().map { it?.toQueue() }
    }

    override suspend fun getQueue(): Queue? {
        return dao.getQueue()?.toQueue()
    }

    override suspend fun createQueue(queue: List<Long>) {
        dao.initQueue(queue)
    }

    override suspend fun setCurrentIndex(index: Int) {
        dao.updateCurrentIndex(index)
    }

    override suspend fun clearQueue() {
        dao.deleteQueue()
    }

    override suspend fun updatePosition(songId: Long, position: Int) {
        dao.updatePosition(songId, position)
    }

    override suspend fun remove(songId: Long) {
        dao.removeItem(songId)
    }

    override suspend fun removeAt(index: Int) {
        dao.removeAt(index)
    }

    override suspend fun add(songId: Long, position: Int) {
        dao.addItem(songId, position)
    }
}