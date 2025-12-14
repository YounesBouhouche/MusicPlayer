package younesbouhouche.musicplayer.core.presentation.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.stateInVM(
    initialValue: T,
    scope: CoroutineScope,
) = stateIn(scope, SharingStarted.WhileSubscribed(5000L), initialValue)

