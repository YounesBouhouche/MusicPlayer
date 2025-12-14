package younesbouhouche.musicplayer.features.player.domain.controller

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import younesbouhouche.musicplayer.core.domain.ext.volumeDown
import younesbouhouche.musicplayer.core.domain.ext.volumeUp
import younesbouhouche.musicplayer.core.domain.player.PlayerManager
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository
import younesbouhouche.musicplayer.core.domain.repositories.QueueRepository
import younesbouhouche.musicplayer.features.player.domain.events.PlayerEvent
import younesbouhouche.musicplayer.features.player.domain.models.PlayState

class PlayerController(
    val playerManager: PlayerManager,
    val playerStateManager: PlayerStateManager,
    val repository: MusicRepository,
    val queueRepository: QueueRepository,
    val context: Context
) {
    val playerState = playerStateManager.playerState

    @OptIn(UnstableApi::class)
    suspend fun handleEvent(event: PlayerEvent) {
        val player = playerManager.initialize()
        when(event) {
            is PlayerEvent.AddToQueue -> {

            }
            is PlayerEvent.Backward -> {
                player.seekBack()
            }
            PlayerEvent.CycleRepeatMode -> {
                val repeatMode = when (player.repeatMode) {
                    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                    Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                    else -> Player.REPEAT_MODE_OFF
                }
                player.repeatMode = repeatMode
                playerStateManager.updateState {
                    it.copy(repeatMode = repeatMode)
                }
            }
            PlayerEvent.DecreaseVolume -> {
                context.volumeDown()
            }
            is PlayerEvent.Forward -> {
                player.seekForward()
            }
            PlayerEvent.IncreaseVolume -> {
                context.volumeUp()
            }
            PlayerEvent.Next -> {
                player.seekToNext()
            }
            PlayerEvent.Pause -> {
                player.pause()
            }
            PlayerEvent.PauseResume -> {
                if (player.isPlaying) player.pause()
                else player.play()
            }
            is PlayerEvent.Play -> {
                playerManager.play(
                    event.tracks,
                    event.index,
                    shuffleMode = event.shuffle
                )
            }
            is PlayerEvent.PlayNext -> {

            }
            PlayerEvent.Previous -> {
                player.seekToPrevious()
            }
            is PlayerEvent.Remove -> {
                player.removeMediaItem(event.index)
                queueRepository.removeAt(event.index)
            }
            PlayerEvent.ResetSpeed -> {
                player.playbackParameters = player.playbackParameters.withSpeed(1f)
            }
            PlayerEvent.Resume -> {
                player.play()
            }
            is PlayerEvent.Seek -> {
                playerManager.seek(event.index, event.time, event.skipIfSameIndex)
            }
            is PlayerEvent.SeekTime -> {
                playerManager.seek(null, event.time)
            }
            is PlayerEvent.SetPitch -> {
                player.playbackParameters = player.playbackParameters.withPitch(event.pitch)
            }
            is PlayerEvent.SetPlayerVolume -> {
                player.volume = event.volume
            }
            is PlayerEvent.SetRepeatMode -> {
                player.repeatMode = event.repeatMode
                playerStateManager.updateState {
                    it.copy(repeatMode = event.repeatMode)
                }
            }
            is PlayerEvent.SetSpeed -> {
                player.playbackParameters = player.playbackParameters.withSpeed(event.speed)
            }
            is PlayerEvent.SetTimer -> {
                playerStateManager.updateState {
                    it.copy(timer = event.timer)
                }
            }
            is PlayerEvent.SetVolume -> {

            }
            PlayerEvent.Stop -> {
                playerManager.stop()
            }
            is PlayerEvent.Swap -> {
                withContext(Dispatchers.IO) {
                    queueRepository.swapPositions(event.from, event.to)
                }
                player.moveMediaItem(event.from, event.to)
            }
            PlayerEvent.ToggleShuffle -> {
                val shuffleMode = !player.shuffleModeEnabled
                player.shuffleModeEnabled = shuffleMode
                playerStateManager.updateState {
                    it.copy(shuffle = shuffleMode)
                }
            }
        }
    }
}