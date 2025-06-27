package younesbouhouche.musicplayer.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository

class GetPlaylistUseCase(val mediaRepository: MediaRepository, val dao: AppDao) {
    suspend operator fun invoke(id: Int): List<MusicCard>? {
        return dao.getPlaylist(id)?.items?.mapNotNull { item ->
            mediaRepository.suspendGetMediaByPath(item)
        }
    }
}