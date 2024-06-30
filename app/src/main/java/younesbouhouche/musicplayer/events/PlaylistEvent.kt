package younesbouhouche.musicplayer.events

import younesbouhouche.musicplayer.models.Playlist

sealed interface PlaylistEvent {
    data object CreateNew: PlaylistEvent
    data object AddToPlaylist: PlaylistEvent
    data class Reorder(val playlist: Playlist, val from: Int, val to: Int): PlaylistEvent
    data class RemoveAt(val playlist: Playlist, val index: Int): PlaylistEvent
    data class DeletePlaylist(val playlist: Playlist): PlaylistEvent
}