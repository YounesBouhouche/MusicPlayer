package younesbouhouche.musicplayer.main.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val image: String? = null,
    val items: List<String> = emptyList(),
) {
    fun createM3UText() =
        "#EXTINF:$name\n#EXTM3U\n" +
            items.joinToString("\n")
}
