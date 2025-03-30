package younesbouhouche.musicplayer.main.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import younesbouhouche.musicplayer.main.data.networking.constructUrl
import younesbouhouche.musicplayer.main.data.networking.safeCall
import younesbouhouche.musicplayer.main.domain.models.DeezerResponse
import younesbouhouche.musicplayer.main.domain.repo.ArtistsRepo
import younesbouhouche.musicplayer.main.domain.util.NetworkError
import younesbouhouche.musicplayer.main.domain.util.Result

class ArtistsRepoImpl(private val client: HttpClient): ArtistsRepo {
    override suspend fun getArtist(name: String): Result<DeezerResponse, NetworkError> {
        return safeCall<DeezerResponse> {
            client.get(constructUrl("search/artist/")) {
                url {
                    parameters.append("q", name)
                    parameters.append("limit", "1")
                }
            }.body()
        }
    }
}