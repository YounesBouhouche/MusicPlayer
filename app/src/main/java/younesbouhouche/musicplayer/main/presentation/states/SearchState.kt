package younesbouhouche.musicplayer.main.presentation.states

import younesbouhouche.musicplayer.core.domain.models.MusicCard

data class SearchState(
    val query: String = "",
    val result: List<MusicCard> = emptyList(),
    val expanded: Boolean = false,
)
