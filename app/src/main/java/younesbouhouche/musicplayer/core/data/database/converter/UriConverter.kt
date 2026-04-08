package younesbouhouche.musicplayer.core.data.database.converter

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class UriConverter {
    @TypeConverter
    fun stringToUri(value: String?): Uri? = value?.toUri()

    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()
}
