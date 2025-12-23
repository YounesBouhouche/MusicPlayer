package younesbouhouche.musicplayer.features.main.presentation.routes.metadata_editor

import android.net.Uri

data class UiState(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val trackNumber: String = "",
    val discNumber: String = "",
    val year: String = "",
    val genre: String = "",
    val composer: String = "",
    val albumArtist: String = "",
    val lyrics: String = "",
    val image: Uri? = null
)
