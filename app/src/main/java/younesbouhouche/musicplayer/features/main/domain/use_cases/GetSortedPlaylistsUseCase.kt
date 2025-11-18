package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.features.main.data.dao.AppDao
import younesbouhouche.musicplayer.features.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.util.sortBy

class GetSortedPlaylistsUseCase(val dao: AppDao) {
    operator fun invoke(sortState: Flow<SortState<ListsSortType>>): Flow<List<Playlist>> =
        combine(dao.getPlaylists(), sortState) { playlists, sortState ->
            playlists.sortBy(sortState.sortType, sortState.ascending)
        }
}