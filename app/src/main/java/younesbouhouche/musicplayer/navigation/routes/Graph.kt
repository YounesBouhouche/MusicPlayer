package younesbouhouche.musicplayer.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class Graph(val ordinal: Int) {
    @Serializable
    data object Permissions : Graph(0)

    @Serializable
    data object Main : Graph(1)

    @Serializable
    data object Settings : Graph(2)

    companion object {
        val graphs = listOf(Permissions, Main, Settings)
    }
}
