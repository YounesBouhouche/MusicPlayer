package younesbouhouche.musicplayer.core.data.database.converter

import android.net.Uri
import androidx.room.TypeConverter
import androidx.core.net.toUri

class UriConverter {
    @TypeConverter
    fun stringToUri(value: String?): Uri? {
        return value?.toUri()
    }

    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return uri?.toString()
    }
}