package younesbouhouche.musicplayer.main.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ArtistModel(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    val picture: String
)
