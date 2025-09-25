package younesbouhouche.musicplayer.core.domain.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import timber.log.Timber
import younesbouhouche.musicplayer.main.presentation.states.PlayState

/**
 * Factory responsible for creating and managing a single ExoPlayer instance
 */
class PlayerFactory(
    private val context: Context,
    private val queueManager: QueueManager,
) {
    // Single ExoPlayer instance for the entire application
    private var exoPlayer: Player? = null

    /**
     * Gets or creates a singleton ExoPlayer instance
     */
    @OptIn(UnstableApi::class)
    @Synchronized
    fun getPlayer(skipSilence: Boolean): Player {
        if (exoPlayer == null) {
            exoPlayer = createNewPlayer(skipSilence)
        } else {
            // Update skip silence setting if player already exists
            //exoPlayer?.skipSilenceEnabled = skipSilence
        }
        return exoPlayer!!
    }

    /**
     * Gets a singleton ExoPlayer instance
     */
    @OptIn(UnstableApi::class)
    @Synchronized
    fun getPlayerOrNull(): Player = exoPlayer ?: createNewPlayer(false).also { exoPlayer = it }

    /**
     * Creates a new ExoPlayer instance with proper configuration
     */
    @OptIn(UnstableApi::class)
    private fun createNewPlayer(skipSilence: Boolean): ExoPlayer {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters())
        }

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                30_000,
                120_000,
                15_000,
                5_000
            )
            .build()

        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(trackSelector)
            .setSkipSilenceEnabled(skipSilence)
            .setLoadControl(loadControl)
            .build().apply {
                playWhenReady = false
            }
    }

    /**
     * Restores player state from database
     */
    suspend fun restorePlayerState(player: Player) {
        Log.i("PlayerFactory", "Restoring player state")
        Log.i("PlayerFactory", "player.mediaItemCount: ${player.mediaItemCount}")
        Log.i("PlayerFactory", "player.playing: ${player.isPlaying}")
        Log.i("PlayerFactory", "casted: ${(player as? ExoPlayer) != null}")
        this.exoPlayer = player
        queueManager.updateIndex(player.currentMediaItemIndex)
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
