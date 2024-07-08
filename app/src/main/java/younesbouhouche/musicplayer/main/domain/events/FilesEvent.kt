package younesbouhouche.musicplayer.main.domain.events

import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.MusicMetadata

sealed interface FilesEvent {
    data object LoadFiles : FilesEvent

    data class AddFile(val file: MusicCard) : FilesEvent

    data class RemoveFile(val file: MusicCard) : FilesEvent

    data class UpdateMetadata(val metadata: MusicMetadata) : FilesEvent
}
