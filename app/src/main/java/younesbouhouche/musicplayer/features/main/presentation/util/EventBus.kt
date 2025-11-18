package younesbouhouche.musicplayer.features.main.presentation.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object EventBus {
    private val _events = Channel<Event>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: Event) {
        _events.send(event)
    }
}