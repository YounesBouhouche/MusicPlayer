package younesbouhouche.musicplayer.features.main.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import younesbouhouche.musicplayer.features.main.data.dao.AppDao
import younesbouhouche.musicplayer.features.main.data.models.ArtistModel
import younesbouhouche.musicplayer.features.main.data.networking.constructUrl
import younesbouhouche.musicplayer.features.main.data.networking.safeCall
import younesbouhouche.musicplayer.features.main.domain.models.DeezerResponse
import younesbouhouche.musicplayer.features.main.domain.repo.ArtistsRepository
import younesbouhouche.musicplayer.features.main.domain.util.NetworkError
import younesbouhouche.musicplayer.features.main.domain.util.Result
import younesbouhouche.musicplayer.features.main.domain.util.map

class ArtistsRepositoryImpl(private val client: HttpClient, private val dao: AppDao): ArtistsRepository {
    override suspend fun getArtist(name: String): Result<String?, NetworkError> {
        return dao.getArtist(name)?.let { Result.Success(it.picture) }
            ?: safeCall<DeezerResponse> {
                client.get(constructUrl("search/artist/")) {
                    url {
                        parameters.append("q", name)
                        parameters.append("limit", "1")
                    }
                }.body()
            }.map {
                val picture = it.data.firstOrNull()?.pictureXl
                dao.upsertArtist(ArtistModel(name, picture ?: ""))
                picture
            }
    }
}