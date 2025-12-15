package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainNavRoute: NavKey {
    @Serializable
    data object Home : MainNavRoute

    @Serializable
    data object Albums : MainNavRoute

    @Serializable
    data class Album(val name: String) : MainNavRoute

    @Serializable
    data object Artists : MainNavRoute

    @Serializable
    data class Artist(val name: String) : MainNavRoute

    @Serializable
    data object Playlists : MainNavRoute

    @Serializable
    data class Playlist(val id: Long) : MainNavRoute

    @Serializable
    data object Library : MainNavRoute

    @Serializable
    data class SongInfo(val songId: Long) : MainNavRoute
}
