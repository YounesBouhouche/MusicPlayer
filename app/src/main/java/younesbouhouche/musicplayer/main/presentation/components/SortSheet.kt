package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import younesbouhouche.musicplayer.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.main.presentation.util.SortBottomSheet
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortSheet(
    sortState: SortState<SortType>,
    modifier: Modifier = Modifier,
    onSortStateChange: (SortState<SortType>) -> Unit,
) = SortBottomSheet(
    sortState,
    SortType.entries,
    { it.icon },
    { it.label },
    onSortStateChange,
    modifier
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSortSheet(
    sortState: SortState<PlaylistSortType>,
    modifier: Modifier = Modifier,
    onSortStateChange: (SortState<PlaylistSortType>) -> Unit,
) = SortBottomSheet(
    sortState,
    PlaylistSortType.entries,
    { it.icon },
    { it.label },
    onSortStateChange,
    modifier
)