package younesbouhouche.musicplayer.main.presentation.util

import kotlinx.serialization.Serializable
import younesbouhouche.musicplayer.core.domain.models.ColsCount

@Serializable
data class SortState<T>(
    val sortType: T,
    val colsCount: ColsCount? = null,
    val expanded: Boolean = false,
    val ascending: Boolean = true,
)