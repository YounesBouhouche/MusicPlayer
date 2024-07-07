package younesbouhouche.musicplayer.core.presentation.util.functions

import younesbouhouche.musicplayer.main.domain.models.MusicCard

fun MusicCard.search(query: String) =
    (title to query).containEachOther() or
            (path to query).containEachOther() or
            (album to query).containEachOther() or
            (artist to query).containEachOther()
