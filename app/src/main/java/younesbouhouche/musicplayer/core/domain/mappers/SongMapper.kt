package younesbouhouche.musicplayer.core.domain.mappers

import younesbouhouche.musicplayer.core.data.database.entities.SongEntity
import younesbouhouche.musicplayer.core.data.database.entities.SongWithState
import younesbouhouche.musicplayer.core.domain.models.Song

fun SongWithState.toSong() = Song(
    id = song.id,
    contentUri = song.contentUri,
    fileName = song.fileName,
    title = song.title,
    artist = song.artist,
    album = song.album,
    duration = song.duration,
    path = song.path,
    date = song.date,
    size = song.size,
    trackNumber = song.trackNumber,
    discNumber = song.discNumber,
    year = song.year,
    composer = song.composer,
    genre = song.genre,
    coverPath = song.coverPath,
    coverUri = song.coverUri,
    isFavorite = state?.isFavorite ?: false,
    playHistory = playHistory
)

fun SongEntity.toSong() = Song(
    id = id,
    contentUri = contentUri,
    fileName = fileName,
    title = title,
    artist = artist,
    album = album,
    duration = duration,
    path = path,
    date = date,
    size = size,
    trackNumber = trackNumber,
    discNumber = discNumber,
    year = year,
    composer = composer,
    genre = genre,
    coverPath = coverPath,
    coverUri = coverUri,
    playHistory = emptyList()
)