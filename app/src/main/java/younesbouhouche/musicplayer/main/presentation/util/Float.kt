package younesbouhouche.musicplayer.main.presentation.util

import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.roundToInt

// Write floating extension function that scales the float value to the given scale
fun Float.scale(scale: Int): Float = (this * 10f.pow(scale)).roundToInt() / 10f.pow(scale)

// Write float extension function that rounds the float value to the given scale string
fun Float.round(scale: Int): String = this.scale(scale).toBigDecimal().setScale(scale, RoundingMode.FLOOR).toString()
