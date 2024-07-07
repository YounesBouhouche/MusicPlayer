package younesbouhouche.musicplayer.main.domain.events

import android.graphics.Bitmap
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.MusicMetadata
import younesbouhouche.musicplayer.main.presentation.states.PlaylistViewState
import younesbouhouche.musicplayer.main.presentation.states.ViewState

sealed interface UiEvent {
    data object ShowSpeedDialog: UiEvent
    data object HideSpeedDialog: UiEvent
    data class SetViewState(val viewState: ViewState): UiEvent
    data class SetPlaylistViewState(val playlistViewState: PlaylistViewState): UiEvent
    data object ExpandPlaylist: UiEvent
    data object CollapsePlaylist: UiEvent
    data class ShowBottomSheet(val item: MusicCard): UiEvent
    data object HideBottomSheet: UiEvent
    data class ShowListBottomSheet(
        val list: List<Long>,
        val title: String,
        val text: String,
        val image: Bitmap? = null,
        val icon: ImageVector
    ): UiEvent
    data object HideListBottomSheet: UiEvent
    data object ShowTimerDialog: UiEvent
    data object HideTimerDialog: UiEvent
    data object ShowNewPlaylistDialog: UiEvent
    data class UpdateNewPlaylistName(val newName: String): UiEvent
    data object HideNewPlaylistDialog: UiEvent
    data class ShowAddToPlaylistDialog(val items: List<String>): UiEvent
    data object HideAddToPlaylistDialog: UiEvent
    data class UpdateSelectedPlaylist(val index: Int): UiEvent
    data class ShowPlaylistBottomSheet(val index: Int): UiEvent
    data object HidePlaylistBottomSheet: UiEvent
    data class ShowMetadataDialog(val metadata: MusicMetadata): UiEvent
    data object HideMetadataDialog: UiEvent
    data object ToggleLyrics: UiEvent
    data object EnableSyncing: UiEvent
    data object DisableSyncing: UiEvent
    data class ShowDetails(val file: MusicCard): UiEvent
    data object DismissDetails: UiEvent
}