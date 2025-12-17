package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class MainNavRoute(val isDialog: Boolean = false): NavKey {
    @Serializable
    data object Home : MainNavRoute()

    @Serializable
    data object Albums : MainNavRoute()

    @Serializable
    data class Album(val name: String) : MainNavRoute()

    @Serializable
    data object Artists : MainNavRoute()

    @Serializable
    data class Artist(val name: String) : MainNavRoute()

    @Serializable
    data object Playlists : MainNavRoute()

    @Serializable
    data class Playlist(val id: Long) : MainNavRoute()

    @Serializable
    data object CreatePlaylist : MainNavRoute(true)

    @Serializable
    data class AddToPlaylist(val ids: List<Long>) : MainNavRoute(true)

    @Serializable
    data object Library : MainNavRoute()

    @Serializable
    data class SongInfo(val songId: Long) : MainNavRoute(true)

    companion object {
        val routes = listOf(
            Home,
            Albums,
            Artists,
            Playlists,
            CreatePlaylist,
            Library
        )
    }
}
