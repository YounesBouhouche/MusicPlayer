package younesbouhouche.musicplayer.core.presentation.util.functions

fun (Pair<String, String>).containEachOther() =
    first.contains(second) or second.contains(first)