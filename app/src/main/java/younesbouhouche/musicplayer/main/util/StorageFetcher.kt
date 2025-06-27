package younesbouhouche.musicplayer.main.util

import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import okio.buffer
import okio.source
import java.io.File

class AppSpecificStorageFetcher(private val data: String) : Fetcher {

    override suspend fun fetch(): FetchResult {
        val file = File(data)
        return SourceResult(
            source = file.source().buffer() as ImageSource,
            mimeType = "image/jpeg",
            dataSource = DataSource.DISK
        )
    }

    class Factory : Fetcher.Factory<String> {
        override fun create(data: String, options: Options, imageLoader: ImageLoader): Fetcher? {
            // Only handle our specific app storage paths
            return if (data.contains("/Android/data/") && data.contains("/files/Pictures/covers/")) {
                AppSpecificStorageFetcher(data)
            } else null
        }
    }
}