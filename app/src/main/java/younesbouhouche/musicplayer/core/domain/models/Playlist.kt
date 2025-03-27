package younesbouhouche.musicplayer.core.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import younesbouhouche.musicplayer.main.data.events.PlayerEvent

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val image: String? = null,
    val items: List<String> = emptyList(),
    val favorite: Boolean = false,
) {
    fun createM3UText() = "#EXTINF:$name\n#EXTM3U\n" + items.joinToString("\n")
}
