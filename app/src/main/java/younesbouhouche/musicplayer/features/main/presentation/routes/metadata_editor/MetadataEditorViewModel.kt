package younesbouhouche.musicplayer.features.main.presentation.routes.metadata_editor

import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.younesb.mydesignsystem.domain.Error
import com.younesb.mydesignsystem.presentation.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotWriteException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.ArtworkFactory
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetSongUseCase
import younesbouhouche.musicplayer.features.main.presentation.util.Event
import younesbouhouche.musicplayer.features.main.presentation.util.sendEvent
import younesbouhouche.musicplayer.features.main.util.toFileUri


class MetadataEditorViewModel(
    songId: Long,
    getSongUseCase: GetSongUseCase,
): ViewModel() {
    val song = MutableStateFlow<Resource<Song, Error>>(Resource.Idle)

    private var initialState = UiState()

    val state = MutableStateFlow(UiState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            song.value = Resource.Loading
            song.value = getSongUseCase(songId)?.let { song ->
                initialState = state.updateAndGet {
                    it.copy(
                        title = song.title,
                        artist = song.artist,
                        album = song.album,
                        trackNumber = song.trackNumber?.toString() ?: "",
                        discNumber = song.discNumber?.toString() ?: "",
                        year = song.year?.toString() ?: "",
                        genre = song.genre ?: "",
                        composer = song.composer ?: "",
                        image = song.coverUri,
                    )
                }
                Resource.Success(song)
            } ?: Resource.Error(object : Error {})
        }
    }

    fun updateState(update: (UiState) -> UiState) = state.update(update)

    fun resetState() {
        state.value = initialState
    }

    fun confirm() {
        (song.value as? Resource.Success)?.data?.let { song ->
            val writeMetadata = {
                try {
                    val f = AudioFileIO.read(song.path.toFileUri().toUri().toFile())
                    val tag = f.getTag()
                    val fields = mapOf(
                        FieldKey.TITLE to state.value.title,
                        FieldKey.ALBUM to state.value.album,
                        FieldKey.ARTIST to state.value.artist,
                        FieldKey.ALBUM_ARTIST to state.value.albumArtist,
                        FieldKey.TRACK to state.value.trackNumber,
                        FieldKey.DISC_NO to state.value.discNumber,
                        FieldKey.COMPOSER to state.value.composer,
                        FieldKey.GENRE to state.value.genre,
                        FieldKey.YEAR to state.value.year
                    )
                    fields.forEach { (key, value) ->
                        try {
                            tag.setField(key, value)
                            state.value.image?.let {
                                tag.setField(ArtworkFactory.createArtworkFromFile(it.toFile()))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    f.commit()
                    sendEvent(Event.ShowSnackBar("Metadata updated successfully"))
                } catch (e: CannotWriteException) {
                    e.printStackTrace()
                    sendEvent(Event.ShowSnackBar("Failed to write metadata"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    sendEvent(Event.ShowSnackBar("Something went wrong"))
                }
            }
            sendEvent(Event.RequestWritePermission(song.contentUri, writeMetadata))
        }
    }
}