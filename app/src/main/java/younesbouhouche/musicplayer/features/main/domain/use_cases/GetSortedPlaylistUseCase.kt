package younesbouhouche.musicplayer.features.main.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.UiPlaylist
import younesbouhouche.musicplayer.features.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.features.main.domain.repo.PlaylistRepository
import younesbouhouche.musicplayer.features.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.features.main.presentation.util.SortState
import younesbouhouche.musicplayer.features.main.util.sortBy

class GetSortedPlaylistUseCase(
    val mediaRepository: MediaRepository,
    val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(id: Flow<Int>, sortState: Flow<SortState<PlaylistSortType>>): Flow<UiPlaylist> =
        combine(
            id,
            mediaRepository.getAllMedia(),
            playlistRepository.getAllPlaylists(),
            sortState,
        ) { id, files, playlists, sortState ->
            val playlist = playlists.firstOrNull { playlist -> playlist.id == id } ?: Playlist(
                id = id,
                name = "Unknown",
                image = null,
                items = emptyList(),
                favorite = false
            )
            val list = playlist.items.mapNotNull { item -> files.firstOrNull { it.path == item } }
            val files = list.sortBy(sortState.sortType, sortState.ascending)
            UiPlaylist(
                id = playlist.id,
                name = playlist.name,
                image = playlist.image,
                items = files,
                favorite = playlist.favorite
            )
        }
}