package younesbouhouche.musicplayer.features.main.presentation.states

sealed interface StartupEvent {
    data object None : StartupEvent

    data object PlayFavorites : StartupEvent

    data object PlayMostPlayed : StartupEvent

    data class PlayPlaylist(val id: Int) : StartupEvent
}
