package younesbouhouche.musicplayer.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Timestamp(
    @PrimaryKey
    val path: String,
    val times: List<LocalDateTime>
)
