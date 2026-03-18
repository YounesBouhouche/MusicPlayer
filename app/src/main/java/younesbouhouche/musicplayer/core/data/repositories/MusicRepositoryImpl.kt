package younesbouhouche.musicplayer.core.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

class MusicRepositoryImpl(
    val songsDao: SongsDao,
    val albumsDao: AlbumsDao,
    val artistsDao: ArtistsDao,
    val playlistDao: PlaylistDao,
    val playHistoryDao: PlayHistoryDao,
    val mediaStoreScanner: MediaStoreScanner,
    val artistsPictureFetcher: ArtistsPictureFetcher
): MusicRepository {
    private val _loadingState = MutableStateFlow(false)
    private val _songsState = MutableStateFlow<List<Song>>(emptyList())
    private val _albumsState = MutableStateFlow<List<Album>>(emptyList())
    private val _artistsState = MutableStateFlow<List<Artist>>(emptyList())
    private val _playlistsState = MutableStateFlow<List<Playlist>>(emptyList())

    val state = _loadingState.asStateFlow()

    override suspend fun refreshMusicLibrary(force: Boolean) {
        if (!force && songsDao.isLibraryInitialized()) {
            getFromCache()
            return
        }

        coroutineScope {
            _loadingState.value = true
            val library = async {
                mediaStoreScanner.scanMediaLibrary()
            }

            launch(Dispatchers.Default) {
                launch {
                    _songsState.value = library.await().songs.map { it.toSong() }
                    val songsWithCovers = async(Dispatchers.Default) {
                        mediaStoreScanner.fetchSongsCover(library.await().songs)
                    }
                    launch(Dispatchers.Default) {
                        _songsState.value = songsWithCovers.await().map { it.toSong() }
                    }
                    launch(Dispatchers.IO) {
                        songsDao.clearSongs()
                        songsDao.upsertSongs(songsWithCovers.await())
                    }
                    val albums = async {
                        mediaStoreScanner.fetchAlbums(songsWithCovers.await())
                    }
                    launch(Dispatchers.Default) {
                        _albumsState.value = albums.await().map { it.toAlbum() }
                    }
                    launch(Dispatchers.IO) {
                        albumsDao.clearAlbums()
                        albumsDao.upsertAlbums(albums.await())
                    }
                }
                launch {
                    launch(Dispatchers.Default) {
                        _artistsState.value = library.await().artists.map { it.toArtist() }
                    }
                    val artistsWithPictures = async(Dispatchers.Default) {
                        artistsPictureFetcher(library.await().artists)
                    }
                    launch(Dispatchers.Default) {
                        _artistsState.value = artistsWithPictures.await().map { it.toArtist() }
                    }
                    artistsDao.clearArtists()
                    artistsDao.upsertArtists(artistsWithPictures.await())
                }
            }

            launch {
                val playlists = async(Dispatchers.Default) {
                    playlistDao.getPlaylists()
                }
                _playlistsState.value = playlists.await().first().map { it.toPlaylist() }
            }
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    override fun getLoadingState(): StateFlow<Boolean> = state

    override suspend fun setFavoriteSong(songId: Long, isFavorite: Boolean) {
        songsDao.setSongFavoriteStatus(songId, isFavorite)
        _songsState.update { songs ->
            songs.map { song ->
                if (song.id == songId) song.copy(isFavorite = isFavorite) else song
            }
        }
    }

    override suspend fun addSongToPlayHistory(songId: Long) {
        val newPlay = PlayHistEntity(songId = songId)
        playHistoryDao.addToPlayHistory(newPlay)
        _songsState.update { songs ->
            songs.map { song ->
                if (song.id == songId) song.copy(playHistory = song.playHistory + newPlay) else song
            }
        }
    }

    override suspend fun clearPlayHistory() {
        playHistoryDao.clearPlayHistory()
        _songsState.update { songs ->
            songs.map { it.copy(playHistory = emptyList()) }
        }
    }

    override fun getSongsList(): Flow<List<Song>> {
        return _songsState.asStateFlow()
    }

    override fun observeSong(id: Long): Flow<Song?> {
        return _songsState.map { songs -> songs.firstOrNull { it.id == id } }
    }

    override suspend fun getSong(id: Long): Song? {
        return _songsState.value.firstOrNull { it.id == id }
    }

    override suspend fun getSongs(ids: List<Long>): List<Song> {
        val songsMap = _songsState.value.associateBy { it.id }
        return ids.mapNotNull { id -> songsMap[id] }
    }

    override fun getRecentlyPlayedSongs(): Flow<List<Song>> {
        return _songsState.map { songs ->
            songs.sortedByDescending { it.playHistory.size }
        }
    }

    override fun getLastAddedSongs(): Flow<List<Song>> {
        return _songsState.map { songs ->
            songs.sortedByDescending { it.date }
        }
    }

    override fun getRecentAlbums(): Flow<List<Album>> {
        return getRecentlyPlayedSongs().map { songs ->
            val albumsByName = _albumsState.value.associateBy { it.name }
            val albumPlayCounts = songs.groupingBy { it.album }.eachCount()
            albumPlayCounts.entries.sortedByDescending { it.value }.mapNotNull { entry ->
                albumsByName[entry.key]
            }.take(5)
        }
    }

    override fun getRecentArtists(): Flow<List<Artist>> {
        return getRecentlyPlayedSongs().map { songs ->
            val artistsByName = _artistsState.value.associateBy { it.name }
            songs.mapNotNull { song -> artistsByName[song.artist] }
                .distinctBy { it.name }
                .take(5)
        }
    }

    override fun getAlbums(): Flow<List<Album>> {
        return _albumsState.asStateFlow()
    }

    override fun getArtists(): Flow<List<Artist>> {
        return _artistsState.asStateFlow()
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return _playlistsState.asStateFlow()
    }

    override suspend fun getAlbum(name: String): Album {
        return _albumsState.value.firstOrNull { it.name == name }?.copy(
            songs = _songsState.value.filter { it.album == name }
        ) ?: throw NoSuchElementException("Album '$name' not found")
    }

    override suspend fun getArtist(name: String): Artist {
        return _artistsState.value.firstOrNull { it.name == name }?.copy(
            songs = _songsState.value.filter { it.artist == name }
        ) ?: throw NoSuchElementException("Artist '$name' not found")
    }

    override fun getPlaylist(id: Long): Flow<Playlist> {
        return _playlistsState.mapNotNull { playlists ->
            playlists.firstOrNull { it.id == id }
        }
    }

    private suspend fun getFromCache() {
        _songsState.value = songsDao.getSongs().first().map { it.toSong() }
        _albumsState.value = albumsDao.suspendGetAlbums().map { it.toAlbum() }
        _artistsState.value = artistsDao.suspendGetArtists().map { it.toArtist() }
        _playlistsState.value = playlistDao.getPlaylists().first().map { it.toPlaylist() }
    }
}