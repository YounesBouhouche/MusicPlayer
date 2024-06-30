package younesbouhouche.musicplayer.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val items: List<String> = emptyList()
)