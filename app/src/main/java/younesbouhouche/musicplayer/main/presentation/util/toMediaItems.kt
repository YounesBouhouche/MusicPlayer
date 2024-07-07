package younesbouhouche.musicplayer.main.presentation.util

import younesbouhouche.musicplayer.main.domain.models.MusicCard

fun List<MusicCard>.toMediaItems() = map { it.toMediaItem() }