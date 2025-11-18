package younesbouhouche.musicplayer.features.main.domain.repo

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.features.main.domain.models.LoadingState

interface MediaRepository {
    suspend fun refreshMediaLibrary(callback: suspend () -> Unit = {})
    fun getMediaById(id: Long): Flow<MusicCard?>
    fun getMediaByPath(path: String): Flow<MusicCard?>

    suspend fun suspendGetMediaById(id: Long): MusicCard?
    suspend fun suspendGetMediaByPath(path: String): MusicCard?
    suspend fun getUriById(id: Long): Uri?
    fun getAllMedia(): Flow<List<MusicCard>>
    fun getFavorites(): Flow<List<MusicCard>>
    fun getAlbums(): Flow<List<Album>>
    fun getArtists(): Flow<List<Artist>>
    fun searchMedia(query: String): Flow<List<MusicCard>>
    fun getFavorite(path: String): Flow<Boolean>
    suspend fun setFavorite(path: String, favorite: Boolean)

    fun getLoading(): StateFlow<LoadingState>
}