package younesbouhouche.musicplayer.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

class TimesListConverter {
    @TypeConverter
    fun fromTimestamp(times: List<LocalDateTime>): String = Gson().toJson(times)

    @TypeConverter
    fun toTimestamp(json: String): List<LocalDateTime> =
        Gson().fromJson(json, (object : TypeToken<List<LocalDateTime>>() {}).type)
}

class PlaylistConverter {
    @TypeConverter
    fun fromPlaylist(playlist: List<String>): String = Gson().toJson(playlist)

    @TypeConverter
    fun toPlaylist(json: String): List<String> =
        Gson().fromJson(json, (object : TypeToken<List<String>>() {}).type)
}
