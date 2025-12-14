package younesbouhouche.musicplayer.core.domain.mappers

import younesbouhouche.musicplayer.core.data.database.entities.AlbumEntity
import younesbouhouche.musicplayer.core.data.database.entities.AlbumWithSongs
import younesbouhouche.musicplayer.core.domain.models.Album

fun AlbumEntity.toAlbum() = Album(
    name = name,
    cover = cover
)

fun AlbumWithSongs.toAlbum() = Album(
    name = album.name,
    cover = album.cover,
    songs = songs.map { it.toSong() }
)