package younesbouhouche.musicplayer.settings.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape


@Composable
fun listItemShape(
    index: Int,
    itemsCount: Int,
    large: Boolean = false,
): Shape {
    val shape = if (large) MaterialTheme.shapes.extraLarge else MaterialTheme.shapes.large
    val smallShape = if (large) MaterialTheme.shapes.small else MaterialTheme.shapes.extraSmall
    return when {
        itemsCount == 1 -> shape
        index == 0 -> shape.copy(bottomStart = smallShape.bottomStart, bottomEnd = smallShape.bottomEnd)
        index == itemsCount - 1 -> shape.copy(topStart = smallShape.topStart, topEnd = smallShape.topEnd)
        else -> smallShape
    }
}
