package younesbouhouche.musicplayer.features.main.presentation.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Task() {
    private var job: Job? = null

    suspend fun start(block: suspend CoroutineScope.() -> Unit) {
        stop()
        job = withContext(Dispatchers.Main) {
            launch(block = block)
        }
    }

    suspend fun startRepeating(
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
