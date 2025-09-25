package younesbouhouche.musicplayer.core.domain.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.room.Entity
import androidx.room.PrimaryKey
import coil.request.ImageRequest
import younesbouhouche.musicplayer.main.data.events.PlayerEvent
import java.io.File

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
    fun search(query: String): Boolean {
        return name.contains(query, ignoreCase = true)
    }
}

@Composable
fun Playlist.getPictureRequest() = with(LocalContext.current) {
    ImageRequest.Builder(this)
        .data(image?.let { File(filesDir, it) })
        .build()
}
