package younesbouhouche.musicplayer.features.main.domain.models

import androidx.room.PrimaryKey
import younesbouhouche.musicplayer.core.domain.models.MusicCard

data class QueueModel(
    @PrimaryKey
    val id: Int = 0,
    val items: List<MusicCard> = emptyList(),
    val index: Int = -1,
) {
    fun getCurrentItem() = items.getOrNull(index)
}