package younesbouhouche.musicplayer.core.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation


data class ArtistWithSongs(
    @Embedded val artist: ArtistEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "artist"
    )
    val songs: List<SongEntity>,
)
