package younesbouhouche.musicplayer.features.main.domain.repo

import kotlinx.coroutines.flow.Flow
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.features.main.domain.events.PlaylistEvent

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getFavoritePlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylist(playlistId: Int): Playlist?
    suspend fun onPlaylistEvent(event: PlaylistEvent)
}