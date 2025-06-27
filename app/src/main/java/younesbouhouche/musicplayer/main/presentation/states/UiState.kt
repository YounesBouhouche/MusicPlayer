package younesbouhouche.musicplayer.main.presentation.states

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.domain.models.LoadingState

data class UiState(
    val showAppName: Boolean = false,
    val loading: LoadingState = LoadingState(),
    val bottomSheetItem: Long? = null,
    val bottomSheetVisible: Boolean = false,
    val playlistId: Int = -1,
    val sheetPlaylistId: Int = -1,
    val playlistBottomSheetVisible: Boolean = false,
    val listBottomSheetList: List<MusicCard>? = null,
    val listBottomSheetTitle: String = "",
    val listBottomSheetImage: Any? = null,
    val listBottomSheetIcon: ImageVector = Icons.AutoMirrored.Default.PlaylistPlay,
    val listBottomSheetVisible: Boolean = false,
    val playlistViewState: PlaylistViewState = PlaylistViewState.COLLAPSED,
    val speedDialog: Boolean = false,
    val timerDialog: Boolean = false,
    val newPlaylistDialog: Boolean = false,
    val newPlaylistName: String = "",
    val newPlaylistImage: Uri? = null,
    val newPlaylistItems: List<String> = emptyList(),
    val addToPlaylistDialog: Boolean = false,
    val addToPlaylistSelected: Set<Int> = emptySet(),
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