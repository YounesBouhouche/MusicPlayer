package younesbouhouche.musicplayer.features.dialog.domain

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState

interface DialogRepo {
    fun getCard(): StateFlow<Song?>
    fun getState(): StateFlow<PlayerState>
    fun play(uri: Uri, scope: CoroutineScope)
    fun pauseResume()
    fun seekTo(ms: Long)
    fun stop()
}