package younesbouhouche.musicplayer.core.domain.ext

import android.content.Context

fun Context.volumeUp() {
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
    audioManager.adjustVolume(
        android.media.AudioManager.ADJUST_RAISE,
        android.media.AudioManager.FLAG_SHOW_UI
    )
}

fun Context.volumeDown() {
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
    audioManager.adjustVolume(
        android.media.AudioManager.ADJUST_LOWER,
        android.media.AudioManager.FLAG_SHOW_UI
    )
}

fun Context.setVolume(value: Float) {
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
    val newVolume = (value * maxVolume).toInt().coerceIn(0, maxVolume)
    audioManager.setStreamVolume(
        android.media.AudioManager.STREAM_MUSIC,
        newVolume,
        android.media.AudioManager.FLAG_SHOW_UI
    )
}