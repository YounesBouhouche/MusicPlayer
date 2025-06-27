package younesbouhouche.musicplayer.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType
import younesbouhouche.musicplayer.main.util.sortBy

class GetSortedMediaUseCase(val mediaRepository: MediaRepository) {
    operator fun invoke(
        sortState: Flow<SortState<SortType>>
    ): Flow<List<MusicCard>> {
        val files = mediaRepository.getAllMedia()
        return combine(files, sortState) { files, state ->
            files.sortBy(state.sortType, state.ascending)
        }
    }
}