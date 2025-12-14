package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class ScanLibraryUseCase(val repository: MusicRepository) {
    suspend operator fun invoke(
        force: Boolean = false,
        onFinished: suspend () -> Unit = {},
        callback: suspend () -> Unit = {}
    ) {
        repository.refreshMusicLibrary(force)
        onFinished()
    }
}