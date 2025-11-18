package younesbouhouche.musicplayer.features.main.data.networking

import younesbouhouche.musicplayer.features.main.domain.util.NetworkError
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import younesbouhouche.musicplayer.features.main.domain.util.Result

suspend inline fun <reified T> safeCall(
    call: () -> HttpResponse
): Result<T, NetworkError> {
    val response = try {
        call()
    } catch (_: UnresolvedAddressException) {
        return Result.Error(NetworkError.NO_INTERNET)
    } catch (_: SerializationException) {
        return Result.Error(NetworkError.SERIALIZATION_ERROR)
    } catch (_: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.Error(NetworkError.UNKNOWN)
    }
    return responseToResult(response)
}