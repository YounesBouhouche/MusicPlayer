package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.features.main.data.dao.AppDao

class GetPlaylistsUseCase(val dao: AppDao) {
    operator fun invoke() = dao.getPlaylists()
}