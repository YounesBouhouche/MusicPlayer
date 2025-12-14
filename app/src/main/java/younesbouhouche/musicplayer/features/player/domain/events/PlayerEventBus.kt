package younesbouhouche.musicplayer.features.player.domain.events

import kotlinx.coroutines.flow.MutableSharedFlow

object PlayerEventBus {
    private val _flow = MutableSharedFlow<PlayerEvent>()

    suspend fun sendEvent(event: PlayerEvent) {
        _flow.emit(event)
    }

    suspend fun collectEvents(onEvent: suspend (PlayerEvent) -> Unit) {
        _flow.collect {
            onEvent(it)
        }
    }
}