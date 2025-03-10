package younesbouhouche.musicplayer.dialog

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.DialogService
import younesbouhouche.musicplayer.core.domain.util.stateInVM
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.PlayState
import younesbouhouche.musicplayer.main.presentation.states.PlayerState
import younesbouhouche.musicplayer.main.presentation.viewmodel.Task

class DialogVM @SuppressLint("StaticFieldLeak") constructor(
    private val context: Context
) : ViewModel() {
    private val _card = MutableStateFlow<MusicCard?>(null)
    val card = _card.stateInVM(null, viewModelScope)

    private val _state = MutableStateFlow(PlayerState(loading = true))
    val state = _state.stateInVM(PlayerState(loading = true), viewModelScope)

    private var controllerFuture: ListenableFuture<MediaController>
    private lateinit var player: Player
    private val observer =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
            }
        }

    protected fun finalize() {
        context.contentResolver.unregisterContentObserver(observer)
    }

    init {
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            observer,
        )
        val sessionToken =
            SessionToken(context, ComponentName(context, DialogService::class.java))
        context.startForegroundService(Intent(context, MediaSessionService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
    }

    fun play(uri: Uri) {
        controllerFuture.addListener({
            player = controllerFuture.get()
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.play()
            player.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        _state.update {
                            it.copy(loading = playbackState == Player.STATE_BUFFERING)
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        _state.update {
                            it.copy(
                                playState =
                                    if (isPlaying) {
                                        PlayState.PLAYING
                                    } else {
                                        PlayState.PAUSED
                                    },
                            )
                        }
                    }
                },
            )
            viewModelScope.launch {
                Task().startRepeating(100L) {
                    _state.update {
                        it.copy(time = player.currentPosition)
                    }
                }
            }
        }, ContextCompat.getMainExecutor(context))
        viewModelScope.launch(Dispatchers.IO) {
            val cursor =
                context.contentResolver.query(
                    uri,
                    arrayOf(
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                    ),
                    null,
                    null,
                    null,
                )
            cursor?.use { crs ->
                val idColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val durationColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val titleColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumIdColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val albumColumn = crs.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                crs.moveToFirst()
                val id = crs.getLong(idColumn)
                val duration = crs.getLong(durationColumn)
                val title = crs.getString(titleColumn)
                val artist = crs.getString(artistColumn)
                val albumId = crs.getLong(albumIdColumn)
                val album = crs.getString(albumColumn)
                val contentUri: Uri =
                    ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id,
                    )
                _card.value =
                    MusicCard
                        .Builder()
                        .setContentUri(contentUri)
                        .setId(id)
                        .setTitle(title)
                        .setArtist(artist)
                        .setAlbum(album)
                        .setAlbumId(albumId)
                        .setDuration(duration)
                        .build()
            }
            val cover =
                with(MediaMetadataRetriever()) {
                    try {
                        setDataSource(context, uri)
                        embeddedPicture?.let {
                            BitmapFactory.decodeByteArray(
                                embeddedPicture,
                                0,
                                embeddedPicture!!.size,
                            )
                        }
                    } catch (_: Exception) {
                        null
                    }
                }
            _card.value = _card.value?.copy(cover = cover)
        }
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun pauseResume() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun stop() {
        player.stop()
        player.release()
    }
}
