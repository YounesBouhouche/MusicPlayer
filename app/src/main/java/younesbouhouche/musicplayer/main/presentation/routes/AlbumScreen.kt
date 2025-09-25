package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.components.ListScreen
import younesbouhouche.musicplayer.main.presentation.models.AlbumUi
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlbumScreen(
    album: AlbumUi,
    sortState: SortState<SortType>,
    onSortStateChange: (SortState<SortType>) -> Unit,
    modifier: Modifier = Modifier,
    onShowBottomSheet: (MusicCard) -> Unit = {},
    onPlay: (index: Int, shuffle: Boolean) -> Unit
) = ListScreen(
    title = album.name,
    cover = album.cover,
    icon = Icons.Default.Album,
    items = album.items,
    modifier = modifier,
    onPlay = onPlay,
    sortState = sortState,
    onSortStateChange = onSortStateChange,
    onShowBottomSheet = onShowBottomSheet
)