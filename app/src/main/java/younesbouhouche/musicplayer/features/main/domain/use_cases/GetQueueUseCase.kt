package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.domain.player.QueueManager
import younesbouhouche.musicplayer.features.main.data.models.Queue
import younesbouhouche.musicplayer.features.main.domain.models.QueueModel
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository

class GetQueueUseCase(
    val mediaRepository: MediaRepository,
    val queueManager: QueueManager
) {
    operator fun invoke(): Flow<QueueModel> {
        val queue = queueManager.getQueue().map { it ?: Queue() }
        val files = mediaRepository.getAllMedia()
        return combine(queue, files) { queue, files ->
            val items = queue.items.mapNotNull { item ->
                files.firstOrNull { it.id == item }
            }
            QueueModel(queue.id, items, queue.index)
        }
    }
}