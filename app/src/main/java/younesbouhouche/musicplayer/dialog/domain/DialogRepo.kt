package younesbouhouche.musicplayer.dialog.domain

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.PlayerState

interface DialogRepo {
    fun getCard(): StateFlow<MusicCard?>
    fun getState(): StateFlow<PlayerState>
    fun play(uri: Uri, scope: CoroutineScope)
    fun pauseResume()
    fun seekTo(ms: Long)
    fun stop()
}