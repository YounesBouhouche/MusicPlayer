package younesbouhouche.musicplayer.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.util.sortBy

class GetArtistsUseCase(val mediaRepository: MediaRepository, val dao: AppDao) {
    operator fun invoke(sortState: Flow<SortState<ListsSortType>>): Flow<List<Artist>> {
        val artists = mediaRepository.getArtists()
        return combine(artists, sortState) { artists, sortState ->
            artists.sortBy(sortState.sortType, sortState.ascending)
        }
    }
}