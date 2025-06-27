package younesbouhouche.musicplayer.main.presentation.util

import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.domain.models.NavRoutes
import younesbouhouche.musicplayer.main.presentation.states.ViewState

sealed interface Event {
    data class SavePlaylist(val playlist: Playlist): Event
    data class SharePlaylist(val playlist: Playlist): Event
    data class Navigate(val route: NavRoutes): Event
    data object ExpandPlayer: Event
}