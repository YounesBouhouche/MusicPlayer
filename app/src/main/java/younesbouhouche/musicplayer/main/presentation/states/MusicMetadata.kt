package younesbouhouche.musicplayer.main.presentation.states

import android.net.Uri

data class MusicMetadata(
    val uri: Uri = Uri.EMPTY,
    val path: String = "",
    val newTitle: String = "",
    val newAlbum: String = "",
    val newArtist: String = "",
    val newGenre: String = "",
    val newComposer: String = "",
    val newYear: String = ""
)
