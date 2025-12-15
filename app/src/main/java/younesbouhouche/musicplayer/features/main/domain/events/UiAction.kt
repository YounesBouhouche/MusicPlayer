package younesbouhouche.musicplayer.features.main.domain.events

import android.net.Uri
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.features.main.presentation.navigation.TopLevelRoutes
import younesbouhouche.musicplayer.features.main.presentation.states.MusicMetadata

sealed interface UiAction {
    data object ShowSpeedDialog : UiAction

    data object HideSpeedDialog : UiAction

    data object ExpandPlaylist : UiAction

    data object CollapsePlaylist : UiAction
    data class SetPlaylist(val id: Int) : UiAction
    data class ShowBottomSheet(val id: Long) : UiAction

    data object HideBottomSheet : UiAction

    data object ShowQueueBottomSheet : UiAction

    data object HideQueueBottomSheet : UiAction

    data class ShowListBottomSheet(
        val list: List<Long>,
        val title: String,
        val image: Any? = null,
        val icon: ImageVector,
    ) : UiAction

    data object HideListBottomSheet : UiAction

    data object ShowTimerDialog : UiAction

    data object HideTimerDialog : UiAction

    data class ShowCreatePlaylistDialog(val items: List<String> = emptyList()) : UiAction

    data class UpdateNewPlaylistName(val newName: String) : UiAction

    data class UpdateNewPlaylistImage(val image: Uri?) : UiAction

    data object HideNewPlaylistDialog : UiAction

    data class ShowAddToPlaylistDialog(val items: List<String>) : UiAction

    data object HideAddToPlaylistDialog : UiAction

    data class UpdateSelectedPlaylist(val id: Int) : UiAction
    data class ShowMetadataDialog(val metadata: MusicMetadata) : UiAction

    data object HideMetadataDialog : UiAction

    data class UpdateMetadata(val metadata: MusicMetadata) : UiAction

    data object ToggleLyrics : UiAction

    data object EnableSyncing : UiAction

    data object DisableSyncing : UiAction

    data object DismissDetails : UiAction

    data class ShowRenamePlaylistDialog(val id: Int, val name: String) : UiAction

    data object HideRenamePlaylistDialog : UiAction

    data class UpdateRenamePlaylistName(val newName: String) : UiAction

    data object ShowPitchDialog : UiAction

    data object HidePitchDialog : UiAction

    data class ShowSortSheet(val route: TopLevelRoutes) : UiAction
}
