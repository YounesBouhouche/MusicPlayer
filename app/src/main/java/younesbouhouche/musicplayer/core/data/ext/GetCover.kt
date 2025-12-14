package younesbouhouche.musicplayer.core.data.ext

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import timber.log.Timber
import java.io.File


fun Context.getCoverContentUri(coverPath: String): Uri? {
    return try {
        val file = File(coverPath)
        if (file.exists()) {
            FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                file
            )
        } else null
    } catch (e: Exception) {
        Timber.tag("MediaRepository").e(e, "Error creating content URI for: $coverPath")
        null
    }
}

fun Context.getCoverUri(coverPath: String): Uri? {
    if (coverPath.isEmpty()) return null
    val file = File(coverPath)
    return if (file.exists()) { "file://${file.absolutePath}".toUri() } else null
}