package younesbouhouche.musicplayer.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository

class GetHistoryUseCase(val mediaRepository: MediaRepository, val dao: AppDao) {
    operator fun invoke(): Flow<List<MusicCard>> {
        val files = mediaRepository.getAllMedia()
        val timestamps = dao.getTimestamps()
        return combine(files, timestamps) { files, timestamps ->
            timestamps
                .sortedByDescending { it.times.maxOrNull() }
                .mapNotNull { item -> files.firstOrNull { item.path == it.path } }
        }
    }
}