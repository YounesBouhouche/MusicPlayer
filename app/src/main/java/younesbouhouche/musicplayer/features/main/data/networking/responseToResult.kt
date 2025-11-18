package younesbouhouche.musicplayer.features.main.data.networking

import younesbouhouche.musicplayer.features.main.domain.util.NetworkError
import younesbouhouche.musicplayer.features.main.domain.util.Result.*
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import younesbouhouche.musicplayer.features.main.domain.util.Result

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, NetworkError> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Success(response.body<T>())
            } catch (_: NoTransformationFoundException) {
                Error(NetworkError.SERIALIZATION_ERROR)
            }
        }
        400 -> Error(NetworkError.SERVER_ERROR)
        401 -> Error(NetworkError.SERVER_ERROR)
        403 -> Error(NetworkError.SERVER_ERROR)
        404 -> Error(NetworkError.SERVER_ERROR)
        408 -> Error(NetworkError.REQUEST_TIMEOUT)
        429 -> Error(NetworkError.TOO_MANY_REQUESTS)
        in 500..599 -> Error(NetworkError.SERVER_ERROR)
        else -> Error(NetworkError.UNKNOWN)
    }
}
