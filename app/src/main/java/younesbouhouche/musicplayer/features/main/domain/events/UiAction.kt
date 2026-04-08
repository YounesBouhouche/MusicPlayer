package younesbouhouche.musicplayer.features.main.domain.events

sealed interface UiAction {
    data class ShowCreatePlaylistDialog(
        val items: List<String> = emptyList(),
    ) : UiAction

    data class ShowAddToPlaylistDialog(
        val items: List<String>,
    ) : UiAction
}
