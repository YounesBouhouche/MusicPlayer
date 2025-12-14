package younesbouhouche.musicplayer.core.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongStateEntity(
    @PrimaryKey(autoGenerate = false)
    val songId: Long,
    val isFavorite: Boolean = false,
)
