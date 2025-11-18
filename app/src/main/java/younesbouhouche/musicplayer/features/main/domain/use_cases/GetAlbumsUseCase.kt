package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.features.main.data.dao.AppDao
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.features.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.util.sortBy

class GetAlbumsUseCase(val mediaRepository: MediaRepository, val dao: AppDao) {
    operator fun invoke(sortState: Flow<SortState<ListsSortType>>): Flow<List<Album>> {
        val albums = mediaRepository.getAlbums()
        return combine(albums, sortState) { albums, sortState ->
            albums.sortBy(sortState.sortType, sortState.ascending)
        }
    }
}