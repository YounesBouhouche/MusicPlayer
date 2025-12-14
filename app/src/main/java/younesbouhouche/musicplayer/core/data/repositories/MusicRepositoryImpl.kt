package younesbouhouche.musicplayer.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import younesbouhouche.musicplayer.core.data.database.dao.AlbumsDao
import younesbouhouche.musicplayer.core.data.database.dao.ArtistsDao
import younesbouhouche.musicplayer.core.data.database.dao.PlayHistoryDao
import younesbouhouche.musicplayer.core.data.database.dao.PlaylistDao
import younesbouhouche.musicplayer.core.data.database.dao.SongsDao
import younesbouhouche.musicplayer.core.data.database.entities.PlayHistEntity
import younesbouhouche.musicplayer.core.data.local.MediaStoreScanner
import younesbouhouche.musicplayer.core.data.remote.ArtistsPictureFetcher
import younesbouhouche.musicplayer.core.domain.mappers.toAlbum
import younesbouhouche.musicplayer.core.domain.mappers.toArtist
import younesbouhouche.musicplayer.core.domain.mappers.toPlaylist
import younesbouhouche.musicplayer.core.domain.mappers.toSong
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository
import younesbouhouche.musicplayer.features.main.domain.models.LoadingState
import kotlin.collections.plusAssign

class MusicRepositoryImpl(
    val songsDao: SongsDao,
    val albumsDao: AlbumsDao,
    val artistsDao: ArtistsDao,
    val playlistDao: PlaylistDao,
    val playHistoryDao: PlayHistoryDao,
    val mediaStoreScanner: MediaStoreScanner,
    val artistsPictureFetcher: ArtistsPictureFetcher
): MusicRepository {

    private val _loadingState = MutableStateFlow(LoadingState(
        step = 1,
        stepsCount = 1,
        progress = 1,
        progressMax = 1
    ))
    val state = _loadingState.asStateFlow()

    override suspend fun refreshMusicLibrary(force: Boolean) {
        if (!force and songsDao.isLibraryInitialized()) {
            return
        }
        _loadingState.value = LoadingState(stepsCount = 3, step = 0, progress = 0, progressMax = 1)
        val library = mediaStoreScanner.scanMediaLibrary { progress, max ->
            _loadingState.update {
                it.copy(progress = progress, progressMax = max)
            }
        }
        _loadingState.update { state ->
            state.copy(step = 1, progress = 0)
        }
        songsDao.clearSongs()
        val songsWithCovers = mediaStoreScanner.fetchSongsCover(library.songs) {
            _loadingState.update { state ->
                state.copy(
                    progress = it,
                    progressMax = library.songs.size
                )
            }
        }
        songsDao.upsertSongs(songsWithCovers)
        albumsDao.clearAlbums()
        albumsDao.upsertAlbums(library.albums)
        artistsDao.clearArtists()
        artistsDao.upsertArtists(library.artists)
        _loadingState.update { state ->
            state.copy(step = 2, progress = 0)
        }
        val artistsWithPictures = artistsPictureFetcher(library.artists) { progress ->
            _loadingState.update { state ->
                state.copy(
                    progress = progress,
                    progressMax = library.artists.size
                )
            }
        }
        artistsDao.upsertArtists(artistsWithPictures)
        _loadingState.update { state ->
            state.copy(step = 3, progress = 0)
        }
    }

    override fun getLoadingState(): StateFlow<LoadingState> = state

    override suspend fun setFavoriteSong(songId: Long, isFavorite: Boolean) {
        songsDao.setSongFavoriteStatus(songId, isFavorite)
    }

    override suspend fun addSongToPlayHistory(songId: Long) {
        playHistoryDao.addToPlayHistory(PlayHistEntity(songId = songId))
    }

    override suspend fun clearPlayHistory() {
        playHistoryDao.clearPlayHistory()
    }

    override fun getSongsList(): Flow<List<Song>> {
        return songsDao.getSongs().map { flow -> flow.map { it.toSong() } }
    }

    override fun getSong(id: Long): Flow<Song?> {
        return songsDao.observeSongById(id).map { it?.toSong() }
    }

    override suspend fun getSongs(ids: List<Long>): List<Song> {
        return ids.map { id -> songsDao.getSongById(id).toSong() }
    }

    override fun getRecentlyPlayedSongs(): Flow<List<Song>> {
        return playHistoryDao.getPlayHistory().map { list ->
            list
                .sortedByDescending { it.playHistory.maxOfOrNull { entity -> entity.playedAt } }
                .map { it.toSong() }
        }
    }

    override fun getAlbums(): Flow<List<Album>> {
        return albumsDao.getAlbums().map { flow -> flow.map { it.toAlbum() } }
    }

    override fun getArtists(): Flow<List<Artist>> {
        return artistsDao.getArtists().map { flow -> flow.map { it.toArtist() } }
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { flow -> flow.map { it.toPlaylist() } }
    }

    override suspend fun getAlbum(name: String): Album {
        return albumsDao.getAlbum(name).toAlbum()
    }

    override suspend fun getArtist(name: String): Artist {
        return artistsDao.getArtist(name).toArtist()
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        return playlistDao.getPlaylist(id).toPlaylist()
    }
}