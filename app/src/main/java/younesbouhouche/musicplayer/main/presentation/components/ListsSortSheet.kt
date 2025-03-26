package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortBottomSheet
import younesbouhouche.musicplayer.main.presentation.util.SortState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsSortSheet(
    sortState: SortState<ListsSortType>,
    modifier: Modifier = Modifier,
    onSortStateChange: (SortState<ListsSortType>) -> Unit,
) = SortBottomSheet(
    sortState,
    ListsSortType.entries,
    { it.icon },
    { it.label },
    onSortStateChange,
    modifier
)