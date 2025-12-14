package younesbouhouche.musicplayer.features.main.presentation.routes.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.presentation.util.stateInVM
import younesbouhouche.musicplayer.features.main.domain.use_cases.GetArtistUseCase
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.presentation.util.SortType
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.features.main.util.sortBy

class ArtistViewModel(
    val mainViewModel: MainViewModel,
    getArtistUseCase: GetArtistUseCase,
    artistName: String
): ViewModel() {
    private val _sortState = MutableStateFlow(SortState(SortType.Title))
    val sortState = _sortState.asStateFlow()
    private val _artist = MutableStateFlow(Artist(name = artistName))
    val artist = combine(_artist, _sortState) { artist, sortState ->
        artist.copy(
            songs = artist.songs.sortBy(sortState.sortType, sortState.ascending)
        )
    }.stateInVM(Artist(name = artistName), viewModelScope)

    init {
        viewModelScope.launch {
            _artist.value = getArtistUseCase(artistName)
        }
    }

    fun play(
        tracks: List<Long>,
        index: Int = 0,
        shuffle: Boolean = false
    ) = mainViewModel.play(tracks, index, shuffle)

    fun setSortState(sortState: SortState<SortType>) {
        _sortState.value = sortState
    }
}