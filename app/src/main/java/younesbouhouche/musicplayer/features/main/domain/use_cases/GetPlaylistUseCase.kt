package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.features.main.data.dao.AppDao
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository

class GetPlaylistUseCase(val mediaRepository: MediaRepository, val dao: AppDao) {
    suspend operator fun invoke(id: Int): List<MusicCard>? {
        return dao.getPlaylist(id)?.items?.mapNotNull { item ->
            mediaRepository.suspendGetMediaByPath(item)
        }
    }
}