package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository

class GetLastAddedUseCase(val mediaRepository: MediaRepository) {
    operator fun invoke(): Flow<List<MusicCard>> {
        return mediaRepository.getAllMedia().map { files ->
            files.sortedByDescending { it.date }.take(10)
        }
    }
}