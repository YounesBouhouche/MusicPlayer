package younesbouhouche.musicplayer.states

import younesbouhouche.musicplayer.models.MusicCard

data class SearchState(
    val query: String = "",
    val result: List<MusicCard> = emptyList(),
    val expanded: Boolean = false,
)