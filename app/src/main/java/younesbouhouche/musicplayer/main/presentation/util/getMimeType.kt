package younesbouhouche.musicplayer.main.presentation.util

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import java.util.Locale

fun Uri.getMimeType(resolver: ContentResolver): String? {
    return if (ContentResolver.SCHEME_CONTENT == scheme) resolver.getType(this)
    else
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            MimeTypeMap.getFileExtensionFromUrl(this.toString()).lowercase(Locale.getDefault())
        )
}