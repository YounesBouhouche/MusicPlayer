package younesbouhouche.musicplayer.features.main.presentation.util

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun expressiveRectShape(
    index: Int,
    count: Int,
    smallShape: CornerBasedShape = MaterialTheme.shapes.medium,
    largeShape: CornerBasedShape = MaterialTheme.shapes.extraLarge,
    horizontal: Boolean = false,
    selected: Boolean = false,
): Shape {
    val first = index <= 0
    val last = index >= count - 1
    val shape = when {
        selected -> RoundedCornerShape(100)
        !first and !last -> smallShape
        first and last -> largeShape
        first ->
            if (horizontal) largeShape.copy(
                topEnd = smallShape.topEnd,
                bottomEnd = smallShape.bottomEnd
            )
            else largeShape.copy(
                bottomStart = smallShape.bottomStart,
                bottomEnd = smallShape.bottomEnd
            )
        else ->
            if (horizontal) largeShape.copy(
                topStart = smallShape.topStart,
                bottomStart = smallShape.bottomStart
            )
            else largeShape.copy(topStart = smallShape.topStart, topEnd = smallShape.topEnd)
    }
    return shape
//    val density = LocalDensity.current
//    val size = Size.Zero
//    val topStart by animateFloatAsState(shape.topStart.toPx(size, density))
//    val topEnd by animateFloatAsState(shape.topEnd.toPx(size, density))
//    val bottomStart by animateFloatAsState(shape.bottomStart.toPx(size, density))
//    val bottomEnd by animateFloatAsState(shape.bottomEnd.toPx(size, density))
//    return RoundedCornerShape(
//        topStart,
//        topEnd,
//        bottomStart,
//        bottomEnd
//    )
}