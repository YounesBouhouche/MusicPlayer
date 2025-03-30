package younesbouhouche.musicplayer.main.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import younesbouhouche.musicplayer.core.domain.models.ItemData
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.data.models.ArtistModel
import younesbouhouche.musicplayer.main.data.models.Queue
import younesbouhouche.musicplayer.main.data.models.Timestamp
import younesbouhouche.musicplayer.main.domain.converters.PlaylistConverter
import younesbouhouche.musicplayer.main.domain.converters.QueueConverter
import younesbouhouche.musicplayer.main.domain.converters.TimesListConverter

@Database(
    entities = [ItemData::class, Timestamp::class, Playlist::class, Queue::class, ArtistModel::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(TimesListConverter::class, PlaylistConverter::class, QueueConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val dao: AppDao
}
