package younesbouhouche.musicplayer.features.main.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Timestamp(
    @PrimaryKey
    val path: String,
    val times: List<Long>,
)
