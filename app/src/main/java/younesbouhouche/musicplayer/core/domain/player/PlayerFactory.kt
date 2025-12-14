package younesbouhouche.musicplayer.core.domain.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import kotlinx.coroutines.flow.first
import timber.log.Timber
import younesbouhouche.musicplayer.core.data.datastore.SettingsPreference
import younesbouhouche.musicplayer.core.domain.repositories.PreferencesRepository
import younesbouhouche.musicplayer.core.domain.repositories.QueueRepository

/**
 * Factory responsible for creating and managing a single ExoPlayer instance
 */
class PlayerFactory(
    private val context: Context,
    private val queueRepository: QueueRepository,
    preferencesRepository: PreferencesRepository
) {
    private var exoPlayer: Player? = null
    private val skipSilence = preferencesRepository.get(SettingsPreference.SkipSilence)

    @OptIn(UnstableApi::class)
    suspend fun getPlayer(): Player = exoPlayer ?: createNewPlayer().also { exoPlayer = it }

    @OptIn(UnstableApi::class)
    private suspend fun createNewPlayer(): ExoPlayer {
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
            .setSkipSilenceEnabled(skipSilence.first())
            .setLoadControl(loadControl)
            .build().apply {
                playWhenReady = false
            }
    }

    /**
     * Restores player state from database
     */
    suspend fun restorePlayerState(player: Player) {
        Timber.tag("PlayerFactory").i("Restoring player state")
        Timber.tag("PlayerFactory").i("player.mediaItemCount: ${player.mediaItemCount}")
        Timber.tag("PlayerFactory").i("player.playing: ${player.isPlaying}")
        Timber.tag("PlayerFactory").i("casted: ${(player as? ExoPlayer) != null}")
        this.exoPlayer = player
        queueRepository.setCurrentIndex(player.currentMediaItemIndex)
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
