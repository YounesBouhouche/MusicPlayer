package younesbouhouche.musicplayer.core.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class SongWithState(
    @Embedded
    val song: SongEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "songId"
    )
    val state: SongStateEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "songId"
    )
    val playHistory: List<PlayHistEntity>,
)
