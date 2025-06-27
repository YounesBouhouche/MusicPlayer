package younesbouhouche.musicplayer.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.util.sortBy

class GetAlbumsUseCase(val mediaRepository: MediaRepository, val dao: AppDao) {
    operator fun invoke(sortState: Flow<SortState<ListsSortType>>): Flow<List<Album>> {
        val albums = mediaRepository.getAlbums()
        return combine(albums, sortState) { albums, sortState ->
            albums.sortBy(sortState.sortType, sortState.ascending)
        }
    }
}