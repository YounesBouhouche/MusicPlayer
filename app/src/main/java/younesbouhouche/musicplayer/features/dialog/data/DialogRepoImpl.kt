package younesbouhouche.musicplayer.features.dialog.data

import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.dialog.presentation.service.DialogService
import younesbouhouche.musicplayer.features.dialog.domain.DialogRepo
import younesbouhouche.musicplayer.features.player.domain.models.PlayState
import younesbouhouche.musicplayer.features.player.domain.models.PlayerState
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.Task

class DialogRepoImpl(val context: Context): DialogRepo {
    private val _card = MutableStateFlow<Song?>(null)
    private val _state = MutableStateFlow(PlayerState(loading = true))

    private var controllerFuture: ListenableFuture<MediaController>
    private lateinit var player: Player
    private val observer =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
        }

    override fun getState(): StateFlow<PlayerState> = _state

    override fun getCard(): StateFlow<Song?> = _card

    init {
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            observer,
        )
        val sessionToken =
            SessionToken(context, ComponentName(context, DialogService::class.java))
        context.startForegroundService(Intent(context, DialogService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
    }
    protected fun finalize() {
        context.contentResolver.unregisterContentObserver(observer)
    }

    override fun play(uri: Uri, scope: CoroutineScope) {
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
                            it.copy(
                                loading = playbackState == Player.STATE_BUFFERING,
                                playState =
                                    if (playbackState == Player.STATE_IDLE) PlayState.STOP
                                    else it.playState
                            )
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (player.playbackState != Player.STATE_IDLE)
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
            scope.launch {
                Task().startRepeating(100L) {
                    _state.update {
                        it.copy(time = player.currentPosition)
                    }
                }
            }
        }, ContextCompat.getMainExecutor(context))

        val idFromUri = try {
            ContentUris.parseId(uri)
        } catch (_: Exception) {
            -1L
        }

        val cursor = if (idFromUri != -1L) {
            context.contentResolver.query(
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
                arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ARTIST,
                ),
                "${MediaStore.Audio.Media._ID} = ?",
                arrayOf(idFromUri.toString()),
                null,
            )
        } else {
            null
        }

        cursor?.use { crs ->
            if (crs.moveToFirst()) {
                val idColumn = crs.getColumnIndex(MediaStore.Audio.Media._ID)
            val durationColumn = crs.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val titleColumn = crs.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = crs.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = crs.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val albumColumn = crs.getColumnIndex(MediaStore.Audio.Media.ALBUM)
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
                Song
                    .Builder()
                    .contentUri(contentUri)
                    .id(id)
                    .title(title)
                    .artist(artist)
                    .album(album)
                    .duration(duration)
                    .build()
            }
        }
        val cover =
            with(MediaMetadataRetriever()) {
                try {
                    setDataSource(context, uri)
                    embeddedPicture ?: ByteArray(0)
                } catch (_: Exception) {
                    ByteArray(0)
                }
            }
//        _card.value = _card.value?.copy(coverPath = cover)
    }

    override fun seekTo(ms: Long) {
        player.seekTo(ms)
    }

    override fun pauseResume() {
        if (player.isPlaying) player.pause() else player.play()
    }

    override fun stop() {
        player.stop()
        player.release()
    }
}