package younesbouhouche.musicplayer.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Graph(val ordinal: Int): NavKey {
    @Serializable
    data object Permissions : NavKey, Graph(0)

    @Serializable
    data object Main : NavKey, Graph(1)

    @Serializable
    data object Settings : NavKey, Graph(2)

    companion object {
        val graphs = listOf(Permissions, Main, Settings)
    }
}
