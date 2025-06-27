package younesbouhouche.musicplayer.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import timber.log.Timber
import younesbouhouche.musicplayer.BuildConfig
import younesbouhouche.musicplayer.main.data.networking.constructUrl

val appModule = module {
    single<HttpClient> {
        HttpClient {
            defaultRequest {
                header(HttpHeaders.Accept, ContentType.Application.Json)
                url(constructUrl(BuildConfig.BASE_URL))
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.tag("HttpClient").i(message)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
    }
    includes(databaseModule, repoModule, viewModelModule, utilsModule, useCaseModule)
}
