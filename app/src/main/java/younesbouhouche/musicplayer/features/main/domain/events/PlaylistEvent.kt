package younesbouhouche.musicplayer.features.main.domain.events

import android.net.Uri

sealed interface PlaylistEvent {
    data class CreateNew(val name: String, val items: List<String>, val image: Uri?) : PlaylistEvent

    data class AddToPlaylist(val ids: Set<Int>, val items: List<String>) : PlaylistEvent

    data class RenamePlaylist(val id: Int, val name: String) : PlaylistEvent

    data class SetFavorite(val id: Int, val favorite: Boolean) : PlaylistEvent

    data class SetCover(val index: Int, val cover: String) : PlaylistEvent
}
