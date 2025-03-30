package younesbouhouche.musicplayer.main.domain.repo

import younesbouhouche.musicplayer.main.domain.models.DeezerResponse
import younesbouhouche.musicplayer.main.domain.util.NetworkError
import younesbouhouche.musicplayer.main.domain.util.Result

interface ArtistsRepo {
    suspend fun getArtist(name: String): Result<DeezerResponse, NetworkError>
}