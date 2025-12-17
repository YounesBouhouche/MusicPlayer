package younesbouhouche.musicplayer.core.domain.mappers

import younesbouhouche.musicplayer.core.data.database.entities.PlaylistWithSongs
import younesbouhouche.musicplayer.core.data.database.entities.SongWithState
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.Song

fun PlaylistWithSongs.toPlaylist() = Playlist(
    id = playlist.id,
    name = playlist.name,
    createdAt = playlist.createdAt,
    songs = songs.map { it.toSong() },
    image = playlist.image
)