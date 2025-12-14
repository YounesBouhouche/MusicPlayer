package younesbouhouche.musicplayer.core.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "queue_song_cross_ref",
    primaryKeys = ["queueId", "songId"],
    foreignKeys = [
        ForeignKey(
            entity = QueueEntity::class,
            parentColumns = ["id"],
            childColumns = ["queueId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE,
            deferred = false
        )
    ],
)
data class QueueSongCrossRef(
    val queueId: Long,
    val songId: Long,
    val position: Int,
)
