package younesbouhouche.musicplayer.features.main.domain.events

sealed interface PlayerEvent {
    data class UpdateFavorite(val path: String, val favorite: Boolean) : PlayerEvent

    data class SetVolume(val volume: Float) : PlayerEvent

    data class PlayIds(val items: List<Long>, val index: Int = 0) : PlayerEvent

    data object PlayFavorites: PlayerEvent

    data object PlayMostPlayed: PlayerEvent

    data class PlayPlaylist(val id: Int): PlayerEvent
}
