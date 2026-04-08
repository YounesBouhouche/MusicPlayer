package younesbouhouche.musicplayer.core.data.database.entities

import androidx.room.Entity

@Entity(
    tableName = "play_hist_song_cross_ref",
    primaryKeys = ["songId", "playedAt"],
)
data class PlayHistSongCrossRef(
    val songId: Long,
    val playedAt: Long,
)
