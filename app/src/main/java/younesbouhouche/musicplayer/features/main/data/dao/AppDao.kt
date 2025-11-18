package younesbouhouche.musicplayer.features.main.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import younesbouhouche.musicplayer.features.main.presentation.util.getCurrentTime
import younesbouhouche.musicplayer.features.main.data.models.Queue
import younesbouhouche.musicplayer.features.main.data.models.Timestamp
import younesbouhouche.musicplayer.core.domain.models.ItemData
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.features.main.data.models.ArtistModel

@Dao
interface AppDao {
    @Upsert
    suspend fun upsertItem(item: ItemData)

    @Query("SELECT * from ItemData")
    fun getItemsData(): Flow<List<ItemData>>

    @Query("SELECT path from ItemData WHERE favorite=true")
    fun getFavorites(): Flow<List<String>>

    @Query("SELECT path from ItemData WHERE favorite=true")
    suspend fun suspendGetFavorites(): List<String>

    @Query("SELECT favorite from ItemData WHERE path=:path")
    fun getFavorite(path: String): Flow<Boolean?>

    @Query("SELECT * from Timestamp")
    fun getTimestamps(): Flow<List<Timestamp>>

    @Query("SELECT * from Timestamp")
    suspend fun suspendGetTimestamps(): List<Timestamp>

    @Query("SELECT * from Timestamp WHERE path=:path")
    fun getTimestamps(path: String): Flow<Timestamp?>

    @Query("SELECT * from Timestamp WHERE path=:path")
    suspend fun suspendGetTimestamps(path: String): Timestamp?

    fun getGroupedTimestamps() =
        getTimestamps().map { timestamp ->
            timestamp
                .groupBy { it.path }
                .asSequence()
                .map { entry ->
                    entry.key to entry.value.flatMap { it.times }
                }
                .toMap()
        }

    @Upsert
    suspend fun upsertTimestamp(timestamp: Timestamp)

    suspend fun addTimestamp(path: String) =
        upsertTimestamp(
            Timestamp(
                path,
                (suspendGetTimestamps(path)?.times ?: emptyList()) + getCurrentTime(),
            ),
        )

    @Upsert
    suspend fun upsertPlaylist(playlist: Playlist)

    @Query("UPDATE playlist SET name=:newName WHERE id=:id")
    suspend fun updatePlaylistName(
        id: Int,
        newName: String,
    )

    @Query("UPDATE playlist SET favorite=:favorite WHERE id=:id")
    suspend fun setPlaylistFavorite(
        id: Int,
        favorite: Boolean,
    )

    @Query("SELECT * from Playlist")
    fun getPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * from Playlist WHERE favorite=true")
    fun getFavoritePlaylists(): Flow<List<Playlist>>

    @Query("SELECT * from Playlist WHERE id=:id")
    suspend fun getPlaylist(id: Int): Playlist?

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("DELETE FROM Playlist WHERE id=:id")
    suspend fun deletePlaylistById(id: Int)

    @Query("SELECT * from Queue WHERE id=0")
    fun getQueue(): Flow<Queue?>

    @Query("SELECT * from Queue WHERE id=0")
    suspend fun asyncGetQueue(): Queue?

    @Upsert
    suspend fun upsertQueue(queue: Queue)

    @Query("UPDATE Queue SET items=:list WHERE id=0")
    suspend fun updateQueue(list: List<Long>)

    @Query("UPDATE Queue SET `index`=:index WHERE id=0")
    suspend fun updateCurrentIndex(index: Int)

    @Upsert
    suspend fun upsertArtist(artist: ArtistModel)

    @Query("SELECT * FROM ArtistModel WHERE name=:name")
    suspend fun getArtist(name: String): ArtistModel?
}
