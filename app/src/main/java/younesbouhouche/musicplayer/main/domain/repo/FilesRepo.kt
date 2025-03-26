package younesbouhouche.musicplayer.main.domain.repo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import younesbouhouche.musicplayer.main.domain.events.FilesEvent
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.presentation.states.PlayerState

interface FilesRepo {
    fun init(scope: CoroutineScope, callback: () -> Unit)
    fun finalize()
    fun getFiles(): Flow<List<MusicCard>>
    fun getAlbums(): Flow<List<Album>>
    fun getArtists(): Flow<List<Artist>>
    fun getPlaylists(): Flow<List<Playlist>>
    fun getState(): StateFlow<PlayerState>
    suspend fun onFilesEvent(event: FilesEvent)
    suspend fun onPlayerEvent(event: PlayerEvent)
    suspend fun onPlaylistEvent(event: PlaylistEvent)
    suspend fun loadFiles()
    suspend fun getFilesMetadata(files: List<MusicCard>): List<MusicCard>
    suspend fun isFavorite(path: String): Boolean
}