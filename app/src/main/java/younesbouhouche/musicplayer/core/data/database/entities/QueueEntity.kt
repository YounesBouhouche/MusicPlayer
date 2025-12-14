package younesbouhouche.musicplayer.core.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QueueEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    val currentIndex: Int = -1,
)