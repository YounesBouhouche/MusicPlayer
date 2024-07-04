package younesbouhouche.musicplayer

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import younesbouhouche.musicplayer.models.MusicCard
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

val Long.timeString: String
    get() = String.format(
        "${if (this < 0) "-" else ""}%02d:%02d:%02d",
        (abs(this) / (1000 * 60 * 60)) % 24,
        (abs(this) / (1000 * 60)) % 60,
        (abs(this) / 1000) % 60
    )

val Long.timerString: String
    get() = String.format(
        "${if (this < 0) "-" else ""}%02d:%02d",
        abs(this) / (1000 * 60),
        (abs(this) / 1000) % 60
    )

// Write floating extension function that scales the float value to the given scale
fun Float.scale(scale: Int): Float = (this * 10f.pow(scale)).roundToInt() / 10f.pow(scale)

// Write float extension function that rounds the float value to the given scale string
fun Float.round(scale: Int): String = this.scale(scale).toBigDecimal().setScale(scale, RoundingMode.FLOOR).toString()

@Composable
fun Int.toDp() = with (LocalDensity.current) { this@toDp.toDp() }

//fun Int.toDp(density: Density) = with (density) { this@toDp.toDp() }

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Uri.getMimeType(resolver: ContentResolver): String? {
    return if (ContentResolver.SCHEME_CONTENT == scheme) resolver.getType(this)
    else
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            MimeTypeMap.getFileExtensionFromUrl(this.toString()).lowercase(Locale.getDefault())
        )
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

fun List<MusicCard>.toMediaItems() = map { it.toMediaItem() }

fun (Pair<String, String>).containEachOther() =
    first.contains(second) or second.contains(first)

fun MusicCard.search(query: String) =
    (title to query).containEachOther() or
            (path to query).containEachOther() or
            (album to query).containEachOther() or
            (artist to query).containEachOther()

fun String.removeLeadingTime(): String
        = when {
    matches(Regex("^(\\[(\\d{2}:\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 12) -> removeRange(0..11)
    matches(Regex("^(\\[(\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 10) -> removeRange(0..9)
    else -> this
}.trimStart()

fun String.toMs(): Long =
    if (matches(Regex("\\d{2}:\\d{2}:\\d{2}([.:])\\d{2}")))
        (((substring(0, 2).toLongOrNull() ?: 0) * 3600
                + (substring(3, 5).toLongOrNull() ?: 0) * 60
                + (substring(6, 8).toLongOrNull() ?: 0)) * 1000
                + (substring(9, 11).toLongOrNull() ?: 0))
    else if (matches(Regex("\\d{2}:\\d{2}([.:])\\d{2}")))
        (((substring(0, 2).toLongOrNull() ?: 0) * 60
                + (substring(3, 5).toLongOrNull() ?: 0)) * 1000
                + (substring(6, 8).toLongOrNull() ?: 0))
    else 0