package younesbouhouche.musicplayer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.data.database.entities.ArtistEntity
import younesbouhouche.musicplayer.core.data.database.entities.ArtistWithSongs

@Dao
interface ArtistsDao {
    @Transaction
    @Query("SELECT * FROM ArtistEntity")
    fun getArtists(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM ArtistEntity")
    suspend fun suspendGetArtists(): List<ArtistEntity>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE name = :name LIMIT 1")
    suspend fun getArtist(name: String): ArtistWithSongs

    @Upsert
    suspend fun upsertArtist(artist: ArtistEntity)

    @Upsert
    suspend fun upsertArtists(artists: List<ArtistEntity>)

    @Delete
    suspend fun deleteArtist(artist: ArtistEntity)

    @Query("DELETE FROM ArtistEntity")
    suspend fun clearArtists()
}