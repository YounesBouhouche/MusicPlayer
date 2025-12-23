package younesbouhouche.musicplayer.features.main.presentation.util

import android.net.Uri
import younesbouhouche.musicplayer.core.domain.models.Playlist

sealed interface Event {
    data object Initiate: Event

    data class SavePlaylist(val playlist: Playlist): Event
    data class SharePlaylist(val playlist: Playlist): Event
    data class ShowSnackBar(val message: String): Event
    data object LaunchPlaylistDialog: Event
    data class RequestWritePermission(val uri: Uri, val onGranted: () -> Unit): Event
}