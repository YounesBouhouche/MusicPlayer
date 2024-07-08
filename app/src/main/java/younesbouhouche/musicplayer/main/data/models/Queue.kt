package younesbouhouche.musicplayer.main.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Queue(
    @PrimaryKey
    val id: Int = 0,
    val items: List<Long> = emptyList(),
    val index: Int = -1,
)
