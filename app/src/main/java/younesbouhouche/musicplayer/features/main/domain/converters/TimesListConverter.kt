package younesbouhouche.musicplayer.features.main.domain.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

class TimesListConverter {
    @TypeConverter
    fun fromTimestamp(times: List<LocalDateTime>): String = Gson().toJson(times)

    @TypeConverter
    fun toTimestamp(json: String): List<LocalDateTime> = Gson().fromJson(json, (object : TypeToken<List<LocalDateTime>>() {}).type)
}
