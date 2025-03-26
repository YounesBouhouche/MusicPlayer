package younesbouhouche.musicplayer.core.domain.models

data class FilesData(
    val files: List<MusicCard>,
    val albums: List<Album>,
    val artists: List<Artist>,
)