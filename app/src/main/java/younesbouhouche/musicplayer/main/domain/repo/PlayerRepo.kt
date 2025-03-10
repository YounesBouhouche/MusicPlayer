package younesbouhouche.musicplayer.main.domain.repo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import younesbouhouche.musicplayer.main.data.events.PlayerEvent
import younesbouhouche.musicplayer.main.presentation.states.PlayerState

abstract class PlayerRepo(val playerState: MutableStateFlow<PlayerState>) {
    abstract fun init(scope: CoroutineScope, callback: () -> Unit)
    abstract fun finalize()
    abstract suspend fun onPlayerEvent(event: PlayerEvent)
}