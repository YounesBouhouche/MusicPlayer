package younesbouhouche.musicplayer.main.domain.events

import android.net.Uri
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.UiPlaylist

sealed interface PlaylistEvent {
    data class CreateNewPlaylist(val name: String, val items: List<String>) : PlaylistEvent

    data class CreateNew(val name: String, val items: List<String>, val image: Uri?) : PlaylistEvent

    data class AddToPlaylist(val index: Int, val items: List<String>) : PlaylistEvent

    data class Reorder(val playlist: UiPlaylist, val from: Int, val to: Int) : PlaylistEvent

    data class RemoveAt(val playlist: UiPlaylist, val index: Int) : PlaylistEvent

    data class DeleteUiPlaylist(val playlist: UiPlaylist) : PlaylistEvent

    data class DeletePlaylist(val playlist: Playlist) : PlaylistEvent

    data class RenamePlaylist(val id: Int, val name: String) : PlaylistEvent

    data class SetFavorite(val id: Int, val favorite: Boolean) : PlaylistEvent
}
