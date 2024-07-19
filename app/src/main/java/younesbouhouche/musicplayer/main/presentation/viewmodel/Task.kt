package younesbouhouche.musicplayer.main.presentation.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Task(private val scope: CoroutineScope) {
    private var job: Job? = null

    fun start(block: suspend CoroutineScope.() -> Unit) {
        stop()
        job = scope.launch(block = block)
    }

    fun startRepeating(
        delay: Long,
        block: suspend CoroutineScope.() -> Unit,
    ) = start {
        while (true) {
            block()
            delay(delay)
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
