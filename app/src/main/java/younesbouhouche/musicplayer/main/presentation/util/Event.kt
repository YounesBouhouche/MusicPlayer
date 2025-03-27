package younesbouhouche.musicplayer.main.presentation.util

import younesbouhouche.musicplayer.core.domain.models.Playlist

sealed interface Event {
    data class SavePlaylist(val playlist: Playlist): Event
    data class SharePlaylist(val playlist: Playlist): Event
}