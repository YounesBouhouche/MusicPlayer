package younesbouhouche.musicplayer

import younesbouhouche.musicplayer.states.MusicMetadata

sealed interface FilesEvent {
    data object LoadFiles: FilesEvent
    data class AddFile(val file: MusicCard): FilesEvent
    data class RemoveFile(val file: MusicCard): FilesEvent
    data class UpdateMetadata(val metadata: MusicMetadata): FilesEvent
}