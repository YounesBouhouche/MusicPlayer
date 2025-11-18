package younesbouhouche.musicplayer.features.main.presentation.util

import younesbouhouche.musicplayer.features.main.presentation.states.StartupEvent

fun String?.toStartupEvent(): StartupEvent {
    return when (this) {
        "favorites" -> StartupEvent.PlayFavorites
        "mostPlayed" -> StartupEvent.PlayMostPlayed
        "playlist" -> StartupEvent.PlayPlaylist(-1)
        else -> StartupEvent.None
    }
}