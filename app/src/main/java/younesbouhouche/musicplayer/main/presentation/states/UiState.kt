package younesbouhouche.musicplayer.main.presentation.states

import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.main.domain.models.MusicCard

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
    val metadata: MusicMetadata = MusicMetadata(),
    val renamePlaylistDialogVisible: Boolean = false,
    val renamePlaylistId: Int = -1,
    val renamePlaylistName: String = "",
)
