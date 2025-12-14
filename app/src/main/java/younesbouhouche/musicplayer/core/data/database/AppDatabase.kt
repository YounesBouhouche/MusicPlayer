package younesbouhouche.musicplayer.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import younesbouhouche.musicplayer.core.data.database.converter.UriConverter
import younesbouhouche.musicplayer.core.data.database.dao.AlbumsDao
import younesbouhouche.musicplayer.core.data.database.dao.ArtistsDao
import younesbouhouche.musicplayer.core.data.database.dao.PlayHistoryDao
import younesbouhouche.musicplayer.core.data.database.dao.PlaylistDao
import younesbouhouche.musicplayer.core.data.database.dao.QueueDao
import younesbouhouche.musicplayer.core.data.database.dao.SongsDao
import younesbouhouche.musicplayer.core.data.database.entities.AlbumEntity
import younesbouhouche.musicplayer.core.data.database.entities.ArtistEntity
import younesbouhouche.musicplayer.core.data.database.entities.PlayHistEntity
import younesbouhouche.musicplayer.core.data.database.entities.PlayHistSongCrossRef
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistEntity
import younesbouhouche.musicplayer.core.data.database.entities.PlaylistSongCrossRef
import younesbouhouche.musicplayer.core.data.database.entities.QueueEntity
import younesbouhouche.musicplayer.core.data.database.entities.QueueSongCrossRef
import younesbouhouche.musicplayer.core.data.database.entities.SongEntity
import younesbouhouche.musicplayer.core.data.database.entities.SongStateEntity

@Database(
    entities = [
        AlbumEntity::class,
        ArtistEntity::class,
        QueueEntity::class,
        SongEntity::class,
        SongStateEntity::class,
        PlaylistEntity::class,
        PlayHistEntity::class,
        PlaylistSongCrossRef::class,
        PlayHistSongCrossRef::class,
        QueueSongCrossRef::class,
               ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    UriConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract val songsDao: SongsDao
    abstract val artistsDao: ArtistsDao
    abstract val albumsDao: AlbumsDao
    abstract val playlistDao: PlaylistDao
    abstract val playHistoryDao: PlayHistoryDao
    abstract val queueDao: QueueDao
}