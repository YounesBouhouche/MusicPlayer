package younesbouhouche.musicplayer.features.main.presentation.util.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import younesbouhouche.musicplayer.features.main.presentation.util.Event
import younesbouhouche.musicplayer.features.main.presentation.util.EventBus

@Composable
fun CollectEvents(
    callback: (Event) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(key1 = lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            EventBus.events.collect(callback)
        }
    }
}