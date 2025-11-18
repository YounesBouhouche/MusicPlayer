package younesbouhouche.musicplayer.features.main.data.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

fun MediaMetadataRetriever.getThumbnail(context: Context, uri: Uri): ByteArray =
    try {
        setDataSource(context, uri)
        embeddedPicture ?: ByteArray(0)
    } catch (_: Exception) {
        ByteArray(0)
    }
