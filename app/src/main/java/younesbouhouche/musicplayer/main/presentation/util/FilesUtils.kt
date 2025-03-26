package younesbouhouche.musicplayer.main.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveUriImageToInternalStorage(
    context: Context,
    uri: Uri,
    name: String,
): String? {
    // Try to get a Bitmap from the content Uri
    val bitmap =
        try {
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
        } catch (e: IOException) {
            null
        }

    // If a Bitmap was obtained, save it to internal storage
    // If no image was saved, return null
    return bitmap?.let {
        val file = File(context.filesDir, name)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            file.path
        } catch (e: IOException) {
            file.delete()
            null
        }
    }
}
