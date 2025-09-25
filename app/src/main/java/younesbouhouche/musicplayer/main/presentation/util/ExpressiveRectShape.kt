package younesbouhouche.musicplayer.main.presentation.util

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

@Composable
fun expressiveRectShape(
    index: Int,
    count: Int,
    smallShape: CornerBasedShape = MaterialTheme.shapes.medium,
    largeShape: CornerBasedShape = MaterialTheme.shapes.extraLarge,
    horizontal: Boolean = false
): Shape {
    val first = index <= 0
    val last = index >= count - 1
    return when {
        !first and !last -> smallShape
        first and last -> largeShape
        first ->
            if (horizontal) largeShape.copy(topEnd = smallShape.topEnd, bottomEnd = smallShape.bottomEnd)
            else largeShape.copy(bottomStart = smallShape.bottomStart, bottomEnd = smallShape.bottomEnd)
        else ->
            if (horizontal) largeShape.copy(topStart = smallShape.topStart, bottomStart = smallShape.bottomStart)
            else largeShape.copy(topStart = smallShape.topStart, topEnd = smallShape.topEnd)
    }
}