package younesbouhouche.musicplayer.core.domain.mappers

import younesbouhouche.musicplayer.core.data.database.entities.QueueWithSongs
import younesbouhouche.musicplayer.core.domain.models.Queue

fun QueueWithSongs.toQueue() = Queue(
    songs = songs?.mapNotNull { it?.toSong() } ?: emptyList(),
    currentIndex = queue?.currentIndex ?: -1
)