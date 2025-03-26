package younesbouhouche.musicplayer.main.presentation.util

import younesbouhouche.musicplayer.core.domain.models.MusicCard

fun List<MusicCard>.toMediaItems() = map { it.toMediaItem() }
