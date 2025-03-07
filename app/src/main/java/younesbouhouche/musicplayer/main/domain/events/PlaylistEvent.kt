package younesbouhouche.musicplayer.main.domain.events

import younesbouhouche.musicplayer.main.domain.models.Playlist
import younesbouhouche.musicplayer.main.domain.models.UiPlaylist

sealed interface PlaylistEvent {
    data class CreateNewPlaylist(val name: String, val items: List<String>) : PlaylistEvent

    data object CreateNew : PlaylistEvent

    data object AddToPlaylist : PlaylistEvent

    data class Reorder(val playlist: UiPlaylist, val from: Int, val to: Int) : PlaylistEvent

    data class RemoveAt(val playlist: UiPlaylist, val index: Int) : PlaylistEvent

    data class DeleteUiPlaylist(val playlist: UiPlaylist) : PlaylistEvent

    data class DeletePlaylist(val playlist: Playlist) : PlaylistEvent

    data object RenamePlaylist : PlaylistEvent
}
