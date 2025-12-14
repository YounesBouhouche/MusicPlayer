package younesbouhouche.musicplayer.features.dialog.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.dialog.domain.DialogRepo
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState

class DialogVM (
    private val dialogRepo: DialogRepo,
) : ViewModel() {
    val card = dialogRepo.getCard().stateInVM(null, viewModelScope)
    val state = dialogRepo.getState().stateInVM(PlayerState(loading = true), viewModelScope)

    fun play(uri: Uri) = dialogRepo.play(uri, viewModelScope)
    fun pauseResume() = dialogRepo.pauseResume()
    fun seekTo(ms: Long) = dialogRepo.seekTo(ms)
    fun stop() = dialogRepo.stop()

}
