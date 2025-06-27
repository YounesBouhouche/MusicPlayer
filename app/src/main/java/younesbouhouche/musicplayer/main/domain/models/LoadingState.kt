package younesbouhouche.musicplayer.main.domain.models

data class LoadingState(
    val progress: Int = 0,
    val progressMax: Int = 1,
    val step: Int = 0,
    val stepsCount: Int = 1
)
