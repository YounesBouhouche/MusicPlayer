package younesbouhouche.musicplayer.core.domain.mappers

import younesbouhouche.musicplayer.core.data.database.entities.ArtistEntity
import younesbouhouche.musicplayer.core.data.database.entities.ArtistWithSongs
import younesbouhouche.musicplayer.core.domain.models.Artist

fun ArtistEntity.toArtist() = Artist(
    name = name,
    picture = picture,
    coverUri = coverUri
)

fun ArtistWithSongs.toArtist() = Artist(
    name = artist.name,
    picture = artist.picture,
    coverUri = artist.coverUri,
    songs = songs.map { it.toSong() }
)