package younesbouhouche.musicplayer.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import younesbouhouche.musicplayer.core.data.database.dao.SongsDao
import younesbouhouche.musicplayer.core.data.database.AppDatabase
import younesbouhouche.musicplayer.core.data.database.dao.AlbumsDao
import younesbouhouche.musicplayer.core.data.database.dao.ArtistsDao
import younesbouhouche.musicplayer.core.data.database.dao.PlayHistoryDao
import younesbouhouche.musicplayer.core.data.database.dao.PlaylistDao
import younesbouhouche.musicplayer.core.data.database.dao.QueueDao
import younesbouhouche.musicplayer.core.data.datastore.PreferencesDataStore

val databaseModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "media.db"
            )
            .build()
    }
    single<SongsDao> { get<AppDatabase>().songsDao }
    single<QueueDao> { get<AppDatabase>().queueDao }
    single<PlaylistDao> { get<AppDatabase>().playlistDao }
    single<PlayHistoryDao> { get<AppDatabase>().playHistoryDao }
    single<AlbumsDao> { get<AppDatabase>().albumsDao }
    single<ArtistsDao> { get<AppDatabase>().artistsDao }
    single { PreferencesDataStore(androidContext()) }
}