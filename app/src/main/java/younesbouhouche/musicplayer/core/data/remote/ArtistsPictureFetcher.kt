package younesbouhouche.musicplayer.core.data.remote

import com.younesb.mydesignsystem.data.networking.safeCall
import com.younesb.mydesignsystem.domain.Result
import com.younesb.mydesignsystem.domain.map
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import younesbouhouche.musicplayer.core.data.database.entities.ArtistEntity
import younesbouhouche.musicplayer.core.data.util.constructUrl
import younesbouhouche.musicplayer.features.main.domain.models.DeezerResponse

class ArtistsPictureFetcher(
    val client: HttpClient,
) {
    suspend operator fun invoke(
        artists: List<ArtistEntity>,
        onUpdate: (Int) -> Unit = {}
    ): List<ArtistEntity> {
        return artists.mapIndexed { index, artist ->
            if (artist.name == "<unknown>") {
                onUpdate(index + 1)
                return@mapIndexed artist
            }
            (safeCall<DeezerResponse, HttpResponse>(
                { body() },
                { status.value }
            ) {
                client.get(constructUrl("search/artist/")) {
                    url {
                        parameters.append("q", artist.name)
                        parameters.append("limit", "1")
                    }
                }
            }.map {
                onUpdate(index + 1)
                artist.copy(
                    picture = it.data.firstOrNull()?.pictureXl
                )
            } as? Result.Success)?.data ?: artist
        }
    }
}