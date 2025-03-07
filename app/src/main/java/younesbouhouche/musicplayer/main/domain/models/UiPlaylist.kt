package younesbouhouche.musicplayer.main.domain.models

data class UiPlaylist(
    val id: Int = 0,
    val name: String = "",
    val image: String? = null,
    val items: List<MusicCard> = emptyList(),
) {
    fun createM3UText() =
        "#EXTINF:$name\n#EXTM3U\n" +
                items.joinToString("\n")
    fun toPlaylist() = Playlist(
        id = id,
        name = name,
        image = image,
        items = items.map { it.path }
    )
}
