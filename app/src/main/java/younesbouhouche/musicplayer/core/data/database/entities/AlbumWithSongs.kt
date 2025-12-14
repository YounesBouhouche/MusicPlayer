package younesbouhouche.musicplayer.core.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation


data class AlbumWithSongs(
    @Embedded val album: AlbumEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "album"
    )
    val songs: List<SongEntity>,
)
