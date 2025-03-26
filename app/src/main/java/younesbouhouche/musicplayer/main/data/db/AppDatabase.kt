package younesbouhouche.musicplayer.main.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.data.models.Queue
import younesbouhouche.musicplayer.main.data.models.Timestamp
import younesbouhouche.musicplayer.main.domain.converters.PlaylistConverter
import younesbouhouche.musicplayer.main.domain.converters.QueueConverter
import younesbouhouche.musicplayer.main.domain.converters.TimesListConverter
import younesbouhouche.musicplayer.core.domain.models.ItemData
import younesbouhouche.musicplayer.core.domain.models.Playlist

@Database(
    entities = [ItemData::class, Timestamp::class, Playlist::class, Queue::class],
    version = 1,
)
@TypeConverters(TimesListConverter::class, PlaylistConverter::class, QueueConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val dao: AppDao
}
