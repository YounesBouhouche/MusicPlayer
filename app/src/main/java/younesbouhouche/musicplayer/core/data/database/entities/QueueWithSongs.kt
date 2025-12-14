package younesbouhouche.musicplayer.core.data.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class QueueWithSongs(
    @Embedded
    val queue: QueueEntity? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = QueueSongCrossRef::class,
            parentColumn = "queueId",
            entityColumn = "songId"
        )
    )
    val songs: List<SongEntity?>? = emptyList(),
)
