package younesbouhouche.musicplayer.features.main.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.features.settings.data.dataFlow


class PlayerDataStore(private val context: Context) {
    companion object {
        private val Context.playerDataStore by preferencesDataStore(name = "player")
        val REPEAT_KEY = intPreferencesKey("repeat")
        val SHUFFLE_KEY = booleanPreferencesKey("shuffle")
        val SPEED_KEY = floatPreferencesKey("speed")
        val PITCH_KEY = floatPreferencesKey("pitch")
        val REMEMBER_REPEAT = booleanPreferencesKey("remember_repeat")
        val REMEMBER_SHUFFLE = booleanPreferencesKey("remember_shuffle")
        val REMEMBER_SPEED = booleanPreferencesKey("remember_speed")
        val REMEMBER_PITCH = booleanPreferencesKey("remember_pitch")
        val SHOW_VOLUME_SLIDER = booleanPreferencesKey("show_volume_slider")
        val MATCH_PICTURE_COLORS = booleanPreferencesKey("match_picture_colors")
        val SHOW_REPEAT = booleanPreferencesKey("show_repeat")
        val SHOW_SHUFFLE = booleanPreferencesKey("show_shuffle")
        val SHOW_SPEED = booleanPreferencesKey("show_speed")
        val SHOW_PITCH = booleanPreferencesKey("show_pitch")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")
        val SHOW_LYRICS = booleanPreferencesKey("show_lyrics")
        val SKIP_SILENCE_KEY = booleanPreferencesKey("skip_silence")
    }

    private fun <T> playerDataFlow(
        key: Preferences.Key<T>,
        default: T,
    ) = dataFlow(context.playerDataStore, key, default)

    val repeatMode = playerDataFlow(REPEAT_KEY, Player.REPEAT_MODE_OFF)
    val shuffle = playerDataFlow(SHUFFLE_KEY, false)
    val speed = playerDataFlow(SPEED_KEY, 1f)
    val pitch = playerDataFlow(PITCH_KEY, 1f)
    val rememberRepeat = playerDataFlow(REMEMBER_REPEAT, true)
    val rememberShuffle = playerDataFlow(REMEMBER_SHUFFLE, false)
    val rememberSpeed = playerDataFlow(REMEMBER_SPEED, false)
    val rememberPitch = playerDataFlow(REMEMBER_PITCH, false)
    val showVolumeSlider = playerDataFlow(SHOW_VOLUME_SLIDER, false)
    val matchPictureColors = playerDataFlow(MATCH_PICTURE_COLORS, true)
    val showRepeat = playerDataFlow(SHOW_REPEAT, true)
    val showShuffle = playerDataFlow(SHOW_SHUFFLE, true)
    val showSpeed = playerDataFlow(SHOW_SPEED, true)
    val showPitch = playerDataFlow(SHOW_PITCH, true)
    val showTimer = playerDataFlow(SHOW_TIMER, true)
    val showLyrics = playerDataFlow(SHOW_LYRICS, true)
    val skipSilence = playerDataFlow(SKIP_SILENCE_KEY, false)

    suspend fun override(
        repeatMode: Int? = null,
        shuffle: Boolean? = null,
        speed: Float? = null,
        pitch: Float? = null,
    ) {
        context.playerDataStore.edit { preferences ->
            repeatMode?.let { preferences[REPEAT_KEY] = it }
            shuffle?.let { preferences[SHUFFLE_KEY] = it }
            speed?.let { preferences[SPEED_KEY] = it }
            pitch?.let { preferences[PITCH_KEY] = it }
        }
    }

    suspend fun saveSettings(
        matchPictureColors: Boolean? = null,
        rememberRepeat: Boolean? = null,
        rememberShuffle: Boolean? = null,
        rememberSpeed: Boolean? = null,
        rememberPitch: Boolean? = null,
        showVolumeSlider: Boolean? = null,
        showRepeat: Boolean? = null,
        showShuffle: Boolean? = null,
        showSpeed: Boolean? = null,
        showPitch: Boolean? = null,
        showTimer: Boolean? = null,
        showLyrics: Boolean? = null,
        skipSilence: Boolean? = null,
    ) {
        context.playerDataStore.edit { preferences ->
            rememberRepeat?.let { preferences[REMEMBER_REPEAT] = it }
            rememberShuffle?.let { preferences[REMEMBER_SHUFFLE] = it }
            rememberSpeed?.let { preferences[REMEMBER_SPEED] = it }
            rememberPitch?.let { preferences[REMEMBER_PITCH] = it }
            showVolumeSlider?.let { preferences[SHOW_VOLUME_SLIDER] = it }
            showRepeat?.let { preferences[SHOW_REPEAT] = it }
            showShuffle?.let { preferences[SHOW_SHUFFLE] = it }
            showSpeed?.let { preferences[SHOW_SPEED] = it }
            showPitch?.let { preferences[SHOW_PITCH] = it }
            showTimer?.let { preferences[SHOW_TIMER] = it }
            showLyrics?.let { preferences[SHOW_LYRICS] = it }
            skipSilence?.let { preferences[SKIP_SILENCE_KEY] = it }
            matchPictureColors?.let { preferences[MATCH_PICTURE_COLORS] = it }
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            context.playerDataStore.edit { preferences ->
                if (!rememberSpeed.first()) preferences[SPEED_KEY] = 1f
                if (!rememberPitch.first()) preferences[PITCH_KEY] = 1f
            }
        }
    }
}
