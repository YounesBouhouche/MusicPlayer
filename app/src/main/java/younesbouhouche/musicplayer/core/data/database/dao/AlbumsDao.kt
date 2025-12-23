package younesbouhouche.musicplayer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.data.database.entities.AlbumEntity
import younesbouhouche.musicplayer.core.data.database.entities.AlbumWithSongs

@Dao
interface AlbumsDao {
    @Query("SELECT * FROM AlbumEntity ORDER BY name ASC")
    fun getAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM AlbumEntity ORDER BY name ASC")
    suspend fun suspendGetAlbums(): List<AlbumEntity>

    @Transaction
    @Query("SELECT * FROM AlbumEntity WHERE name=:name")
    suspend fun getAlbum(name: String): AlbumWithSongs

    @Upsert
    suspend fun upsertAlbum(album: AlbumEntity)

    @Upsert
    suspend fun upsertAlbums(albums: List<AlbumEntity>)

    @Delete
    suspend fun deleteAlbum(album: AlbumEntity)

    @Query("DELETE FROM AlbumEntity")
    suspend fun clearAlbums()
}