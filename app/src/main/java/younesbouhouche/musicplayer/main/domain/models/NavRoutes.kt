package younesbouhouche.musicplayer.main.domain.models

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {
    @Serializable
    data object Home : NavRoutes()

    @Serializable
    data object Albums : NavRoutes()

    @Serializable
    data class Album(val title: String) : NavRoutes()

    @Serializable
    data object Artists : NavRoutes()

    @Serializable
    data class Artist(val name: String) : NavRoutes()

    @Serializable
    data object Playlists : NavRoutes()

    @Serializable
    data object Library : NavRoutes()

    @Serializable
    data class Playlist(val playlistId: Int) : NavRoutes()

    @Serializable
    data object Favorites : NavRoutes()

    @Serializable
    data object MostPlayedScreen : NavRoutes()

    @Serializable
    data object HistoryScreen : NavRoutes()

    @Serializable
    data object LastAddedScreen : NavRoutes()
}