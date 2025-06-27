package younesbouhouche.musicplayer.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository

class GetMediaByIdUseCase(val mediaRepository: MediaRepository) {
    operator fun invoke(ids: List<Long>): Flow<List<MusicCard>> {
        val files = mediaRepository.getAllMedia()
        return files.map {
            ids.mapNotNull { id ->
                it.firstOrNull { file -> file.id == id }
            }
        }
    }
}