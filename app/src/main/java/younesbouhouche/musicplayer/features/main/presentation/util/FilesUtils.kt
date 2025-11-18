package younesbouhouche.musicplayer.features.main.presentation.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
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
        } catch (_: IOException) {
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
        } catch (_: IOException) {
            file.delete()
            null
        }
    }
}

fun Context.createTempFile(name: String, content: String): File {
    val tempFile = File.createTempFile(name, null, cacheDir)
    FileOutputStream(tempFile).use {
        it.write(content.toByteArray())
    }
    return tempFile
}

fun Context.shareFile(file: File, fileType: String) {
    val fileUri = FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        file
    )
    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_TEXT, file.name)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = fileType
            },
            "Share file",
        ),
    )
}
