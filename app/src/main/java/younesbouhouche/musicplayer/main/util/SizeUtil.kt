package younesbouhouche.musicplayer.main.util

import android.util.Range
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

fun Long.toReadableFileSize(): String {
    val suffixes = arrayOf("B", "KB", "MB", "GB", "TB")
    if (this <= 0) return "0 B"
    val exp = (ln(this.toDouble()) / ln(1024.0)).toInt()
    val value = (this / 1024.0.pow(exp.toDouble())).roundToInt()
    return String.format(Locale.getDefault(), "%d %s", value, suffixes[exp])
}

fun Long.getSizeRange(): Range<Long> {
    return when (this) {
        in 0L..10_240L -> Range(0L, 10_240L) // 0B - 10KB
        in 10_241L..102_400L -> Range(10_241L, 102_400L) // 10KB - 100KB
        in 102_401L..1_048_576L -> Range(102_401L, 1_048_576L) // 100KB - 1MB
        in 1_048_577L..10_485_760L -> Range(1_048_577L, 10_485_760L) // 1MB - 10MB
        in 10_485_761L..104_857_600L -> Range(10_485_761L, 104_857_600L) // 10MB - 100MB
        in 104_857_601L..1_073_741_824L -> Range(104_857_601L, 1_073_741_824L) // 100MB - 1GB
        else -> Range(1_073_741_825L, Long.MAX_VALUE) // > 1GB
    }
}

fun Range<Long>.toReadableDurationString(): String {
    val lower = this.lower.toReadableFileSize()
    val upper = if (this.upper == Long.MAX_VALUE) "∞" else this.upper.toReadableFileSize()
    return "$lower - $upper"
}