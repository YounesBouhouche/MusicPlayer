package younesbouhouche.musicplayer.main.domain.events

import android.net.Uri
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.domain.models.Routes
import younesbouhouche.musicplayer.main.presentation.states.MusicMetadata

sealed interface UiEvent {
    data object ShowSpeedDialog : UiEvent

    data object HideSpeedDialog : UiEvent

    data object ExpandPlaylist : UiEvent

    data object CollapsePlaylist : UiEvent

    data class SharePlaylist(val playlist: Playlist): UiEvent

    data class SetPlaylist(val id: Int) : UiEvent
    data class ShowBottomSheet(val id: Long) : UiEvent

    data object HideBottomSheet : UiEvent

    data object ShowQueueBottomSheet : UiEvent

    data object HideQueueBottomSheet : UiEvent

    data class ShowListBottomSheet(
        val list: List<Long>,
        val title: String,
        val image: Any? = null,
        val icon: ImageVector,
    ) : UiEvent

    data object HideListBottomSheet : UiEvent

    data object ShowTimerDialog : UiEvent

    data object HideTimerDialog : UiEvent

    data class ShowCreatePlaylistDialog(val items: List<String> = emptyList()) : UiEvent

    data class UpdateNewPlaylistName(val newName: String) : UiEvent

    data class UpdateNewPlaylistImage(val image: Uri?) : UiEvent

    data object HideNewPlaylistDialog : UiEvent

    data class ShowAddToPlaylistDialog(val items: List<String>) : UiEvent

    data object HideAddToPlaylistDialog : UiEvent

    data class UpdateSelectedPlaylist(val index: Int) : UiEvent

    data class SavePlaylist(val playlist: Playlist): UiEvent

    data class ShowMetadataDialog(val metadata: MusicMetadata) : UiEvent

    data object HideMetadataDialog : UiEvent

    data class UpdateMetadata(val metadata: MusicMetadata) : UiEvent

    data object ToggleLyrics : UiEvent

    data object EnableSyncing : UiEvent

    data object DisableSyncing : UiEvent

    data class ShowDetails(val file: MusicCard) : UiEvent

    data object DismissDetails : UiEvent

    data class ShowRenamePlaylistDialog(val id: Int, val name: String) : UiEvent

    data object HideRenamePlaylistDialog : UiEvent

    data class UpdateRenamePlaylistName(val newName: String) : UiEvent

    data object ShowPitchDialog : UiEvent

    data object HidePitchDialog : UiEvent

    data class ShowSortSheet(val route: Routes) : UiEvent
}
