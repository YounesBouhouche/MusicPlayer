package younesbouhouche.musicplayer.main.domain.repo

import younesbouhouche.musicplayer.main.domain.util.NetworkError
import younesbouhouche.musicplayer.main.domain.util.Result

interface ArtistsRepository {
    suspend fun getArtist(name: String): Result<String?, NetworkError>

}