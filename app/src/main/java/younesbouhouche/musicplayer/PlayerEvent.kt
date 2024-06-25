package younesbouhouche.musicplayer

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.states.MusicMetadata
import younesbouhouche.musicplayer.states.PlaylistViewState
import younesbouhouche.musicplayer.states.ViewState

sealed interface PlayerEvent {
    data class AddToQueue(val items: List<MusicCard>): PlayerEvent
    data class AddToNext(val items: List<MusicCard>): PlayerEvent
    data class Play(val items: List<MusicCard>, val index: Int = 0): PlayerEvent
    data class PlayPaths(val items: List<String>, val index: Int = 0): PlayerEvent
    data object Resume: PlayerEvent
    data object Pause: PlayerEvent
    data object PauseResume: PlayerEvent
    data object Stop: PlayerEvent
    data object Next: PlayerEvent
    data object Previous: PlayerEvent
    data class Seek(val index: Int, val time: Long = 0L): PlayerEvent
    data class SeekTime(val time: Long): PlayerEvent
    data class Forward(val ms: Long): PlayerEvent
    data class Backward(val ms: Long): PlayerEvent
    data class Swap(val from: Int, val to: Int): PlayerEvent
    data class Remove(val index: Int): PlayerEvent
    data class SetRepeatMode(val repeatMode: Int): PlayerEvent
    data class SetSpeed(@FloatRange(from = 0.0, fromInclusive = false) val speed: Float): PlayerEvent
    data object CycleRepeatMode: PlayerEvent
    data object ToggleShuffle: PlayerEvent
    data object ResetSpeed: PlayerEvent
    data class SetTimer(val timer: TimerType): PlayerEvent
    data class UpdateFavorite(val path: String, val favorite: Boolean): PlayerEvent
    data class SetFavorite(val path: String): PlayerEvent
    data class ToggleFavorite(val path: String): PlayerEvent
}

sealed interface PlaylistEvent {
    data object CreateNew: PlaylistEvent
    data object AddToPlaylist: PlaylistEvent
    data class Reorder(val playlist: Playlist, val from: Int, val to: Int): PlaylistEvent
    data class RemoveAt(val playlist: Playlist, val index: Int): PlaylistEvent
    data class DeletePlaylist(val playlist: Playlist): PlaylistEvent
}

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
        val list: List<MusicCard>,
        val title: String,
        val text: String,
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
}