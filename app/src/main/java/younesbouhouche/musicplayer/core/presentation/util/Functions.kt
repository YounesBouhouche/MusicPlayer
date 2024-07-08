package younesbouhouche.musicplayer.core.presentation.util

import younesbouhouche.musicplayer.main.domain.models.MusicCard

fun (Pair<String, String>).containEachOther() = first.contains(second) or second.contains(first)

fun String.removeLeadingTime(): String =
    when {
        matches(Regex("^(\\[(\\d{2}:\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 12) -> removeRange(0..11)
        matches(Regex("^(\\[(\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 10) -> removeRange(0..9)
        else -> this
    }.trimStart()

fun MusicCard.search(query: String) =
    (title to query).containEachOther() or
        (path to query).containEachOther() or
        (album to query).containEachOther() or
        (artist to query).containEachOther()
