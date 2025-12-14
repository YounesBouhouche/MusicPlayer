package younesbouhouche.musicplayer.core.domain.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import java.io.File


data class Playlist(
    val id: Long = 0,
    val name: String = "",
    val image: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val songs: List<Song> = emptyList(),
) {
    fun createM3UContent(): String {
        val builder = StringBuilder()
        builder.append("#EXTM3U\n")
        for (song in songs) {
            builder.append("#EXTINF:${song.duration / 1000},${song.title}\n")
            builder.append("${song.contentUri}\n")
        }
        return builder.toString()
    }
    fun search(query: String) = name.lowercase().contains(query.lowercase())
}

@Composable
fun Playlist.getPictureRequest() = with(LocalContext.current) {
    ImageRequest.Builder(this)
        .data(image?.let { File(filesDir, it) })
        .build()
}