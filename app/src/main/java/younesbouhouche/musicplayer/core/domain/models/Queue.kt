package younesbouhouche.musicplayer.core.domain.models

data class Queue(
    val songs: List<Song> = emptyList(),
    val currentIndex: Int = -1
) {
    fun getCurrentItem() = songs.getOrNull(currentIndex)
}