package younesbouhouche.musicplayer.main.domain.events

sealed interface MetadataEvent {
    data class Title(val title: String) : MetadataEvent

    data class Album(val album: String) : MetadataEvent

    data class Artist(val artist: String) : MetadataEvent

    data class Genre(val genre: String) : MetadataEvent

    data class Composer(val composer: String) : MetadataEvent

    data class Year(val year: String) : MetadataEvent
}
