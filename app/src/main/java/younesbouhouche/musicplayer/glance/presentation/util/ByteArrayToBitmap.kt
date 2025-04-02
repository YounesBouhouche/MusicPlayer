package younesbouhouche.musicplayer.glance.presentation.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun ByteArray.toBitmap(width: Int = 200, height: Int = 200): Bitmap? {
    // First decode with inJustDecodeBounds=true to check dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeByteArray(this, 0, this.size, options)

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, width, height)

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeByteArray(this, 0, size, options)
}