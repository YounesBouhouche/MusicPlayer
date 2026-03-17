package younesbouhouche.musicplayer.features.main.domain.use_cases

import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository

class ScanMediaUseCase(val repo: MusicRepository) {
    suspend operator fun invoke() {
        repo.refreshMusicLibrary(true)
    }
}