package younesbouhouche.musicplayer.features.main.domain.events

import android.net.Uri
import androidx.compose.ui.graphics.vector.ImageVector
import younesbouhouche.musicplayer.features.main.presentation.navigation.TopLevelRoutes
import younesbouhouche.musicplayer.features.main.presentation.states.MusicMetadata

sealed interface UiAction {
    data class ShowCreatePlaylistDialog(val items: List<String> = emptyList()) : UiAction
    data class ShowAddToPlaylistDialog(val items: List<String>) : UiAction
}
