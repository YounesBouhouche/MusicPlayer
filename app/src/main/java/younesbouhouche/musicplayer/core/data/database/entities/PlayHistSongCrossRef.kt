package younesbouhouche.musicplayer.core.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "play_hist_song_cross_ref",
    primaryKeys = ["songId", "playedAt"]
)
data class PlayHistSongCrossRef(
    val songId: Long,
    val playedAt: Long,
)