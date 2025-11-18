package younesbouhouche.musicplayer.features.main.domain.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class QueueConverter {
    @TypeConverter
    fun fromQueue(queue: List<Long>): String = Gson().toJson(queue)

    @TypeConverter
    fun toQueue(json: String): List<Long> = Gson().fromJson(json, (object : TypeToken<List<Long>>() {}).type)
}
