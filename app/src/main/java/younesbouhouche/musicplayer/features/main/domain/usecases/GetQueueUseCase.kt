package younesbouhouche.musicplayer.features.main.domain.usecases

import younesbouhouche.musicplayer.core.domain.repositories.QueueRepository

class GetQueueUseCase(
    val repository: QueueRepository,
) {
    operator fun invoke() = repository.observeQueue()
}
