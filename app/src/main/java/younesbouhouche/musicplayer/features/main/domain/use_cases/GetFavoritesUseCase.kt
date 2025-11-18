package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.presentation.util.SortType
import younesbouhouche.musicplayer.features.main.util.sortBy

class GetFavoritesUseCase(val mediaRepository: MediaRepository) {
    operator fun invoke(sortState: Flow<SortState<SortType>>): Flow<List<MusicCard>> {
        val favorites = mediaRepository.getAllMedia().map { files ->
            files.filter { it.favorite }
        }
        return combine(favorites, sortState) { files, state ->
            files.sortBy(state.sortType, state.ascending)
        }
    }
}