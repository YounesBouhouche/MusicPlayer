package younesbouhouche.musicplayer.core.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemData(
    @PrimaryKey
    val path: String,
    val favorite: Boolean = false,
)
