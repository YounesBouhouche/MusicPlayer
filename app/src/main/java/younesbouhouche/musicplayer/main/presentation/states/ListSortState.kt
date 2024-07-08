package younesbouhouche.musicplayer.main.presentation.states

import younesbouhouche.musicplayer.main.domain.models.ColsCount
import younesbouhouche.musicplayer.main.domain.models.ListsSortType

data class ListSortState(
    val sortType: ListsSortType = ListsSortType.Name,
    val expanded: Boolean = false,
    val ascending: Boolean = true,
    val colsCount: ColsCount = ColsCount.One,
    val colsExpanded: Boolean = false,
)
