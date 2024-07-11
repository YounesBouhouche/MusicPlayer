package younesbouhouche.musicplayer.core.presentation.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import java.time.ZonedDateTime

fun (Pair<String, String>).containEachOther() = first.contains(second) or second.contains(first)

fun String.removeLeadingTime(): String =
    when {
        matches(Regex("^(\\[(\\d{2}:\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 12) -> removeRange(0..11)
        matches(Regex("^(\\[(\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 10) -> removeRange(0..9)
        else -> this
    }.trimStart()

fun MusicCard.search(query: String) =
    (title to query).containEachOther() or
        (path to query).containEachOther() or
        (album to query).containEachOther() or
        (artist to query).containEachOther()

fun getCurrentTime(): Long = ZonedDateTime.now().toInstant().toEpochMilli()

fun Context.getAppVersion(): String =
    (
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    ).versionName
