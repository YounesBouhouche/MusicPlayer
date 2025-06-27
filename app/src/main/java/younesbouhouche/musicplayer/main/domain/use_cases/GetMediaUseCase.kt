package younesbouhouche.musicplayer.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType
import younesbouhouche.musicplayer.main.util.sortBy

class GetMediaUseCase(val mediaRepository: MediaRepository) {
    operator fun invoke(): Flow<List<MusicCard>> =mediaRepository.getAllMedia()
}