package younesbouhouche.musicplayer.main.domain.use_cases

import younesbouhouche.musicplayer.main.data.dao.AppDao

class GetPlaylistsUseCase(val dao: AppDao) {
    operator fun invoke() = dao.getPlaylists()
}