package younesbouhouche.musicplayer.main.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.data.dao.AppDao
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.domain.repo.PlaylistRepository
import younesbouhouche.musicplayer.main.presentation.util.saveUriImageToInternalStorage

class PlaylistRepositoryImpl(
    private val context: Context,
    private val dao: AppDao
) : PlaylistRepository {
    override suspend fun getPlaylist(playlistId: Int): Playlist? {
        return dao.getPlaylist(playlistId)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return dao.getPlaylists()
    }

    override fun getFavoritePlaylists(): Flow<List<Playlist>> {
        return dao.getFavoritePlaylists()
    }

    override suspend fun onPlaylistEvent(event: PlaylistEvent) {
        when(event) {
            is PlaylistEvent.AddToPlaylist -> {
                event.ids.forEach { id ->
                    val playlist = dao.getPlaylist(id) ?: return
                    if (playlist.items.containsAll(event.items)) return
                    dao.upsertPlaylist(
                        playlist.copy(items = playlist.items + event.items)
                    )
                }
            }
            is PlaylistEvent.CreateNew -> {
                val fileName = "pl_${getAllPlaylists().first().size}.jpg"
                val saved =
                    event.image?.let {
                        withContext(Dispatchers.IO) {
                            saveUriImageToInternalStorage(context, it, fileName) != null
                        }
                    } == true
                dao.upsertPlaylist(
                    Playlist(
                        name = event.name,
                        items = event.items,
                        image = fileName.takeIf { saved }
                    )
                )
            }
            is PlaylistEvent.DeletePlaylist -> dao.deletePlaylist(event.playlist)
            is PlaylistEvent.DeleteUiPlaylist -> dao.deletePlaylistById(event.playlist.id)
            is PlaylistEvent.RemoveAt -> {
                val playlist = dao.getPlaylist(event.playlist.id) ?: return
                if (event.index < 0 || event.index >= playlist.items.size) return
                dao.upsertPlaylist(
                    playlist.copy(items = playlist.items.toMutableList().apply {
                        removeAt(event.index)
                    })
                )
            }
            is PlaylistEvent.RenamePlaylist -> {
                dao.updatePlaylistName(event.id, event.name)
            }
            is PlaylistEvent.Reorder -> {
                val playlist = dao.getPlaylist(event.playlist.id) ?: return
                if (event.from < 0 || event.from >= playlist.items.size ||
                    event.to < 0 || event.to >= playlist.items.size) return
                val items = playlist.items.toMutableList().apply {
                    add(event.to, removeAt(event.from))
                }
                dao.upsertPlaylist(playlist.copy(items = items))
            }
            is PlaylistEvent.SetFavorite -> {
                val playlist = dao.getPlaylist(event.id) ?: return
                dao.upsertPlaylist(playlist.copy(favorite = event.favorite))
            }
            is PlaylistEvent.SetCover -> {
                val playlist = dao.getPlaylist(event.index) ?: return
                dao.upsertPlaylist(
                    playlist.copy(image = event.cover)
                )
            }
        }
    }

}
