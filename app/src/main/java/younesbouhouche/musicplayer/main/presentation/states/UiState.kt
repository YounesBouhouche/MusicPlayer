package younesbouhouche.musicplayer.main.presentation.states

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.core.domain.models.MusicCard

data class UiState(
    val loading: Boolean = false,
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
    val newPlaylistImage: Uri? = null,
    val newPlaylistItems: List<String> = emptyList(),
    val addToPlaylistDialog: Boolean = false,
    val addToPlaylistIndex: Int = 0,
    val addToPlaylistItems: List<String> = emptyList(),
    val metadataDialog: Boolean = false,
    val detailsDialog: Boolean = false,
    val pitchDialog: Boolean = false,
    val detailsFile: MusicCard? = null,
    val lyricsVisible: Boolean = false,
    val syncing: Boolean = true,
    val metadata: MusicMetadata = MusicMetadata(),
    val renamePlaylistDialogVisible: Boolean = false,
    val renamePlaylistId: Int = -1,
    val renamePlaylistName: String = "",
    val queueSheetVisible: Boolean = false,
    val showVolumeSlider: Boolean = false,
    val showRepeat: Boolean = true,
    val showShuffle: Boolean = true,
    val showSpeed: Boolean = true,
    val showPitch: Boolean = true,
    val showTimer: Boolean = true,
    val showLyrics: Boolean = true
)
