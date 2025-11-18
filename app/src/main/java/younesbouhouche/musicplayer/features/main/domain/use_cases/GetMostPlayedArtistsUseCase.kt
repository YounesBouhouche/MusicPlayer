package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.features.main.data.dao.AppDao
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository

class GetMostPlayedArtistsUseCase(val mediaRepository: MediaRepository, val dao: AppDao) {
    operator fun invoke(): Flow<List<Artist>> {
        val artists = mediaRepository.getArtists()
        val files = mediaRepository.getAllMedia()
        val timestamps = dao.getTimestamps()
        return combine(artists, files, timestamps) { artists, files, timestamps ->
            artists
                .asSequence()
                .filter { it.name != "<unknown>" }
                .map { artist ->
                    artist to
                            artist.items
                                .mapNotNull { item -> files.firstOrNull { it.id == item } }
                                .sumOf { item ->
                                    timestamps.firstOrNull { item.path == it.path }
                                        ?.times
                                        ?.size ?: 0
                                }
                }
                .filter { it.second > 0 }
                .sortedByDescending { it.second }
                .map { it.first }
                .toList()
        }
    }
}