package younesbouhouche.musicplayer.core.domain.player

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.data.models.Queue

class QueueManager(val dao: AppDao) {
    fun getQueue() = dao.getQueue()
    suspend fun getCurrentIndex() = getQueue().map { it?.id }.first()
    fun getCurrentItem() = dao.getQueue().map {
        it?.items?.getOrNull(it.id)
    }
    suspend fun setQueue(queue: Queue) = dao.upsertQueue(queue)
    suspend fun updateList(list: List<Long>) = dao.upsertQueue(Queue(0, list))
    suspend fun updateList(callback: (List<Long>) -> List<Long>) {
        val currentQueue = getQueue().first()?.items ?: emptyList()
        val updatedQueue = callback(currentQueue)
        updateList(updatedQueue)
    }
    suspend fun removeAt(index: Int) = updateList {
        it.toMutableList().apply {
            removeAt(index)
        }
    }
    suspend fun updateQueue(queue: Queue) = dao.upsertQueue(queue)

    suspend fun updateIndex(index: Int) = dao.updateCurrentIndex(index)
    suspend fun addToQueue(items: List<MusicCard>, index: Int? = null): List<MusicCard> {
        val queue = getQueue().first() ?: Queue()
        val max = queue.items.size
        val list = items.filter { it.id !in queue.items }
        updateList {
            it.toMutableList().apply {
                list.map { item -> item.id }.let { ids ->
                    index?.let { i ->
                        addAll(i.coerceAtMost(max), ids) }
                        ?: addAll(ids)
                }
            }
        }
        return list
    }
}