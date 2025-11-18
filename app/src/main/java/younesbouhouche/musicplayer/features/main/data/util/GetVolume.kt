package younesbouhouche.musicplayer.features.main.data.util

import android.media.AudioManager

fun AudioManager.getVolume() =
    getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() /
            getStreamMaxVolume(AudioManager.STREAM_MUSIC)