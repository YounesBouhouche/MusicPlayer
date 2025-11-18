package younesbouhouche.musicplayer.features.main.domain.repo

import younesbouhouche.musicplayer.features.main.domain.util.NetworkError
import younesbouhouche.musicplayer.features.main.domain.util.Result

interface ArtistsRepository {
    suspend fun getArtist(name: String): Result<String?, NetworkError>

}