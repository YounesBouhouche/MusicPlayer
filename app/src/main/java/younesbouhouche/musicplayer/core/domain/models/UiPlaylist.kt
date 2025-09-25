package younesbouhouche.musicplayer.core.domain.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import java.io.File

data class UiPlaylist(
    val id: Int = 0,
    val name: String = "",
    val image: String? = null,
    val items: List<MusicCard> = emptyList(),
    val favorite: Boolean = false,
) {
    fun createM3UText() =
        "#EXTINF:$name\n#EXTM3U\n" +
                items.joinToString("\n")
    fun toPlaylist() = Playlist(
        id = id,
        name = name,
        image = image,
        items = items.map { it.path },
        favorite = favorite
    )
}


@Composable
fun UiPlaylist.getPictureRequest() = with(LocalContext.current) {
    ImageRequest.Builder(this)
        .data(image?.let { File(filesDir, it) })
        .build()
}

