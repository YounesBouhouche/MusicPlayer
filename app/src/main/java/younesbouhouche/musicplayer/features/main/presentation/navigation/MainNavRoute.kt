package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class MainNavRoute: NavKey {
    @Serializable
    data object Home : MainNavRoute(), NavKey

    @Serializable
    data object Albums : MainNavRoute(), NavKey

    @Serializable
    data class Album(val name: String) : MainNavRoute(), NavKey

    @Serializable
    data object Artists : MainNavRoute(), NavKey

    @Serializable
    data class Artist(val name: String) : MainNavRoute(), NavKey

    @Serializable
    data object Playlists : MainNavRoute(), NavKey

    @Serializable
    data class Playlist(val id: Long) : MainNavRoute(), NavKey

    @Serializable
    data object Library : MainNavRoute(), NavKey

    @Serializable
    data class SongInfo(val songId: Long) : MainNavRoute(), NavKey
}
