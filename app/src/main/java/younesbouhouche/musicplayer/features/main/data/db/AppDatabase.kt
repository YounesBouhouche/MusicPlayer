package younesbouhouche.musicplayer.features.main.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import younesbouhouche.musicplayer.core.domain.models.ItemData
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.features.main.data.dao.AppDao
import younesbouhouche.musicplayer.features.main.data.models.ArtistModel
import younesbouhouche.musicplayer.features.main.data.models.Queue
import younesbouhouche.musicplayer.features.main.data.models.Timestamp
import younesbouhouche.musicplayer.features.main.domain.converters.PlaylistConverter
import younesbouhouche.musicplayer.features.main.domain.converters.QueueConverter
import younesbouhouche.musicplayer.features.main.domain.converters.TimesListConverter

@Database(
    entities = [ItemData::class, Timestamp::class, Playlist::class, Queue::class, ArtistModel::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(TimesListConverter::class, PlaylistConverter::class, QueueConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val dao: AppDao
}
