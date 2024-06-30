package younesbouhouche.musicplayer.events

import younesbouhouche.musicplayer.models.MusicCard
import younesbouhouche.musicplayer.states.MusicMetadata

sealed interface FilesEvent {
    data object LoadFiles: FilesEvent
    data class AddFile(val file: MusicCard): FilesEvent
    data class RemoveFile(val file: MusicCard): FilesEvent
    data class UpdateMetadata(val metadata: MusicMetadata): FilesEvent
}