package younesbouhouche.musicplayer.main.domain.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistConverter {
    @TypeConverter
    fun fromPlaylist(playlist: List<String>): String = Gson().toJson(playlist)

    @TypeConverter
    fun toPlaylist(json: String): List<String> =
        Gson().fromJson(json, (object : TypeToken<List<String>>() {}).type)
}