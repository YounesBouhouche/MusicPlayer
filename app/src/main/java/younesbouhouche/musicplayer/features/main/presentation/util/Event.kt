package younesbouhouche.musicplayer.features.main.presentation.util

import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.features.main.domain.models.NavRoutes

sealed interface Event {
    data object Initiate: Event
    data class SavePlaylist(val playlist: Playlist): Event
    data class SharePlaylist(val playlist: Playlist): Event
    data object LaunchPlaylistDialog: Event
}