package younesbouhouche.musicplayer.main.presentation.states

import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.media3.common.Player
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.events.TimerType

data class PlayerState(
    val index: Int = -1,
    val time: Long = 0,
    val loading: Boolean = false,
    val playState: PlayState = PlayState.STOP,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val shuffle: Boolean = false,
    val speed: Float = 1f,
    var timer: TimerType = TimerType.Disabled,
)

data class UiState(
    val bottomSheetItem: MusicCard? = null,
    val bottomSheetVisible: Boolean = false,
    val listBottomSheetList: List<MusicCard>? = null,
    val listBottomSheetTitle: String = "",
    val listBottomSheetText: String = "",
    val listBottomSheetImage: Bitmap? = null,
    val listBottomSheetIcon: ImageVector = Icons.AutoMirrored.Default.PlaylistPlay,
    val listBottomSheetVisible: Boolean = false,
    val playlistBottomSheetVisible: Boolean = false,
    val viewState: ViewState = ViewState.HIDDEN,
    val playlistViewState: PlaylistViewState = PlaylistViewState.COLLAPSED,
    val speedDialog: Boolean = false,
    val timerDialog: Boolean = false,
    val newPlaylistDialog: Boolean = false,
    val newPlaylistName: String = "",
    val addToPlaylistDialog: Boolean = false,
    val addToPlaylistIndex: Int = 0,
    val addToPlaylistItems: List<String> = emptyList(),
    val metadataDialog: Boolean = false,
    val detailsDialog: Boolean = false,
    val detailsFile: MusicCard? = null,
    val lyricsVisible: Boolean = false,
    val syncing: Boolean = true,
    val metadata: MusicMetadata = MusicMetadata()
)

enum class ViewState {
    HIDDEN, SMALL, LARGE
}

enum class PlaylistViewState {
    COLLAPSED, EXPANDED
}

enum class PlayState {
    PLAYING, PAUSED, STOP
}
