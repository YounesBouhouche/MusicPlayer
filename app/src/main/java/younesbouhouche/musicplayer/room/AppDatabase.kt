package younesbouhouche.musicplayer.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import younesbouhouche.musicplayer.models.Playlist

@Database(
    entities = [ItemData::class,Timestamp::class, Playlist::class],
    version = 1
)
@TypeConverters(TimesListConverter::class, PlaylistConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract val dao: AppDao
}