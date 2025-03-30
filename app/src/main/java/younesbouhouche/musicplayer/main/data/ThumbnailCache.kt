package younesbouhouche.musicplayer.main.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.collection.LruCache
import com.tomclaw.cache.DiskLruCache
import java.io.File
import java.io.FileOutputStream

class ThumbnailCache(val context: Context, cacheSize: Long = 10 * 1024 * 1024) { // 10MB default cache size
    private val diskCache = DiskLruCache.create(
        File(context.cacheDir, "thumbnails"),
        cacheSize
    )

    private val memoryCache = LruCache<String, Bitmap>(
        (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    )

    fun getThumbnail(key: String, loadThumbnail: () -> Bitmap?): Bitmap? {
        // Check memory cache first
        val memoryBitmap = memoryCache[key]
        if (memoryBitmap != null) {
            return memoryBitmap
        }

        // Check disk cache
        val file = diskCache.get(key)
        if (file != null) {
            val bitmap = BitmapFactory.decodeFile(file.path)
            if (bitmap != null) {
                memoryCache.put(key, bitmap)
                return bitmap
            }
        }

        // Load and cache if not found
        val loadedBitmap = loadThumbnail()
        loadedBitmap?.let {
            putThumbnail(context, key, loadedBitmap)
        }
        return loadedBitmap
    }

    private fun putThumbnail(context: Context, key: String, bitmap: Bitmap) {
        val file = File(context.cacheDir, "thumb_$key")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()
        diskCache.put(key, file)
        memoryCache.put(key, bitmap)
    }
}