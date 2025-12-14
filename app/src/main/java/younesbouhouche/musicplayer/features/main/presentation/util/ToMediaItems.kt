package younesbouhouche.musicplayer.features.main.presentation.util

import younesbouhouche.musicplayer.core.domain.models.Song

fun List<Song>.toMediaItems() = map { it.toMediaItem() }
