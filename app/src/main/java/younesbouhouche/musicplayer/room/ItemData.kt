package younesbouhouche.musicplayer.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemData(
    @PrimaryKey
    val path: String,
    val favorite: Boolean = false,
)