package younesbouhouche.musicplayer.states

sealed interface StartupIntent {
    data object None: StartupIntent
    data object PlayFavorites: StartupIntent
    data object PlayMostPlayed: StartupIntent
    data class PlayPlaylist(val id: Int): StartupIntent
}
