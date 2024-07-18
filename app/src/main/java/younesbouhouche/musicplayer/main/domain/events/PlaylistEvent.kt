package younesbouhouche.musicplayer.main.domain.events

import younesbouhouche.musicplayer.main.domain.models.Playlist

sealed interface PlaylistEvent {
    data class CreateNewPlaylist(val name: String, val items: List<String>) : PlaylistEvent

    data object CreateNew : PlaylistEvent

    data object AddToPlaylist : PlaylistEvent

    data class Reorder(val playlist: Playlist, val from: Int, val to: Int) : PlaylistEvent

    data class RemoveAt(val playlist: Playlist, val index: Int) : PlaylistEvent

    data class DeletePlaylist(val playlist: Playlist) : PlaylistEvent

    data object RenamePlaylist : PlaylistEvent
}
